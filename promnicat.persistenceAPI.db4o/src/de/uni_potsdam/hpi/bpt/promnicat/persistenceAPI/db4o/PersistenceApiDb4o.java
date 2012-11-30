/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Observer;
import java.util.UUID;
import java.util.logging.Logger;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.UuidSupport;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ClientConfiguration;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.foundation.NotImplementedException;
import com.db4o.query.Predicate;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.config.ConfigurationParserDb4o;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.PojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;

/**
 * This is the connection to the database to load, save, delete {@link Model}, {@link Revision}, 
 * and {@link Representation} and some nearly arbitrary analysis results.
 * The underlying database is Db4objects.<br>
 * All {@link IPojo}s are given a database id.<br>
 * All outgoing links of a Pojo are saved and loaded as well (not deleted though).
 * In order to increase performance for the very important use case of loading {@link Representation}s, 
 * {@link Representation}s can be loaded as lightweight {@link Representation}s, 
 * which means only the connected {@link Revision} and its {@link Model} are loaded
 * but no sibling or cousin {@link Representation}s or {@link Revision}s.
 * The {@link Revision} and {@link Model} need to be loaded to have metadata and title of the process.
 * <br>
 * Loading {@link Representation} and {@link IPojo}s can be synchronous or asynchronous.
 * Synchronous loading collects all results and returns a list of results, which can be a bottleneck in
 * available memory space. Therefore in asynchronous loading, a {@link Observer} is handed one result at 
 * a time which can then be processed and stored or removed before the next result is handled.
 *  
 * @author Tobias Hoppe
 *
 */
public class PersistenceApiDb4o implements IPersistenceApi {

	private String dbPath = "";
	private int port = 0;
	private String user = "";
	private String password = "";
	private boolean isLocal = false;

	/**
	 * Database access object
	 */
	private ObjectContainer db = null;
	private ObjectServer server = null;

	private final static Logger logger = Logger.getLogger(PersistenceApiDb4o.class.getName());

	/**
	 * @param configurationFilePath the path to the configuration file (relative to java project)
	 * @return the orientDb accessing the database defined in the file
	 */
	public static PersistenceApiDb4o getInstance(String configurationFilePath) {
		try {
			return (PersistenceApiDb4o) new ConfigurationParserDb4o(configurationFilePath).getDbInstance();
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}
		return null;
	}

	/**
	 * Create database with remote access
	 * @param dbPath
	 * @param port
	 * @param user
	 * @param password
	 */
	public PersistenceApiDb4o(String dbPath, int port, String user, String password) {
		this.dbPath = dbPath;
		this.user = user;
		this.port = port;
		this.password = password;
		openDb();
	}

	/**
	 * Create database with local file access
	 * @param dbPath
	 */
	public PersistenceApiDb4o(String dbPath) {
		this.isLocal = true;
		this.dbPath = dbPath;
		openDb();
	}

	@Override
	public IPojoFactory getPojoFactory() {
		return PojoFactory.init();
	}

	@Override
	public void openDb() {
		if(db != null) {
			//database already open, nothing to do here
			return;
		}
		File dbFile = new File(dbPath);
		
		if(!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				logger.severe("Database file could not be created on path " + dbPath);
				e.printStackTrace();
				throw new IllegalStateException();
			}
			logger.info("Created database at " + dbPath);
		}
		
		if(isLocal) {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.common().objectClass(Model.class).cascadeOnUpdate(true);
			config.common().objectClass(Revision.class).cascadeOnUpdate(true);
			config.common().objectClass(Representation.class).cascadeOnUpdate(true);
			config.common().objectClass(IModel.class).generateUUIDs(true);
			config.common().objectClass(IRevision.class).generateUUIDs(true);
			config.common().objectClass(IRepresentation.class).generateUUIDs(true);
			config.common().add(new UuidSupport());
			this.db = Db4oEmbedded.openFile(config, dbPath);
			logger.info("Opened database at " + dbPath);
		} else {
			//remote access
			ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
			clientConfig.common().objectClass(IModel.class).cascadeOnUpdate(true);
			clientConfig.common().objectClass(IRevision.class).cascadeOnUpdate(true);
			clientConfig.common().objectClass(IRepresentation.class).cascadeOnUpdate(true);
			clientConfig.common().objectClass(IModel.class).generateUUIDs(true);
			clientConfig.common().objectClass(IRevision.class).generateUUIDs(true);
			clientConfig.common().objectClass(IRepresentation.class).generateUUIDs(true);
			clientConfig.common().add(new UuidSupport());

			ServerConfiguration serverConfig = Db4oClientServer.newServerConfiguration();
			serverConfig.common().objectClass(IModel.class).cascadeOnUpdate(true);
			serverConfig.common().objectClass(IRevision.class).cascadeOnUpdate(true);
			serverConfig.common().objectClass(IRepresentation.class).cascadeOnUpdate(true);
			serverConfig.common().objectClass(IModel.class).generateUUIDs(true);
			serverConfig.common().objectClass(IRevision.class).generateUUIDs(true);
			serverConfig.common().objectClass(IRepresentation.class).generateUUIDs(true);
			serverConfig.common().add(new UuidSupport());
			this.server = Db4oClientServer.openServer(serverConfig, dbPath, port);
			this.server.grantAccess(user, password);
			
			this.db = Db4oClientServer.openClient(clientConfig, "localhost", port, user, password);
			logger.info("Opened database at " + dbPath);
		}		
	}

	/**
	 * @return database access on object level to be used for direct database inspection.
	 */
	public ObjectContainer getInternalDbAccess() {
		if(db == null) {
			openDb();
		}
		return db;
	}

	@Override
	public void closeDb() {
		db.close();
		if(!isLocal) {
			server.close();
			server = null;
		}
		db = null;
		logger.info("Database closed at " + dbPath);
	}

	@Override
	public void dropDb() {
		File dbFile = new File(dbPath);
		if (dbFile.exists()) {
			closeDb();
			dbFile.delete();
			logger.info("Database dropped at " + dbPath);
		} else {
			logger.info("Could not drop database, no database found on path " + dbPath);
		}
	}

	@Override
	public long countClass(Class<? extends IPojo> aClass) {
		return db.queryByExample(aClass).size();
	}

	@Override
	public void executeCommand(String sqlCommand) {
		//TODO implement me!!
		throw new NotImplementedException();
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- save and delete: ----------------------------------------
	//--------------------------------------------------------------------------------------------

	/**
	 * Tries to save a pojo into the database
	 */
	@Override
	public UUID savePojo(IPojo pojo) {
		db.store(pojo);
		db.commit();
		return pojo.getDbId();
	}

	@Override
	public boolean deleteAllPojosOfClass(Class<?> aClass) {
		for(Object o : db.query(aClass)) {
			db.delete(o);
		}
		db.commit();
		return true;
	}

	@Override
	public boolean deletePojos(final Collection<UUID> dbIds) {
		List<AbstractPojo> results = db.query(new Predicate<AbstractPojo>() {
			private static final long serialVersionUID = -4285232048786172918L;

			public boolean match(AbstractPojo pojo) {
				return dbIds.contains(pojo.getDbId());
			}
		});
		if(results.size() != dbIds.size()) {
			logger.severe("could not delete all of " + dbIds + " because at least one was not found.");
			return false;
		}
		for(AbstractPojo o : results) {
			db.delete(o);
		}
		db.commit();
		return true;
	}

	@Override
	public boolean deletePojo(final UUID dbId) {
		List<AbstractPojo> results = db.query(new Predicate<AbstractPojo>() {
			private static final long serialVersionUID = -4869265179279035775L;

			public boolean match(AbstractPojo pojo) {
				return pojo.getDbId().equals(dbId);
			}
		});
		if(results.size() != 1) {
			logger.severe("could not delete " + dbId + " because it was not found.");
			return false;
		}
		db.delete(results.iterator().next());
		db.commit();
		return true;
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- load: 1 object ------------------------------------------
	//--------------------------------------------------------------------------------------------

	@Override
	public IPojo loadPojo(final UUID dbId) {
		List<IPojo> results = db.query(new Predicate<IPojo>() {
			private static final long serialVersionUID = -4987906785905273681L;

			public boolean match(IPojo pojo) {
				return pojo.getDbId().equals(dbId);
			}
		});
		if(results.size() != 1) {
			throw new IllegalArgumentException("Could not retrieve "+ dbId + " from database.");
		}
		return results.iterator().next();
	}


	@Override
	public IModel loadCompleteModelWithDbId(final UUID dbId) {
		List<Model> results = db.query(new Predicate<Model>() {
			private static final long serialVersionUID = -8342880144810147356L;

			public boolean match(Model model) {
				return model.getDbId().equals(dbId);
			}
		});
		if(results.size() != 1) {
			throw new IllegalArgumentException("Could not retrieve a model with "+ dbId + " from database.");
		}
		return results.iterator().next();
	}

	@Override
	public IModel loadCompleteModelWithImportedId(final String id) {
		List<Model> results = db.query(new Predicate<Model>() {
			private static final long serialVersionUID = -1272996669437643445L;

			public boolean match(Model model) {
				return id.equals(model.getImportedId());
			}
		});
		if(results.size() > 1) {
			logger.severe("Model ids must be unique! But, got "
					+ results.size() + "models with id " + id);
			return null;
		}
		if(results.size() < 1) {
			logger.info("Could not retrieve model with importedId "+ id);
			return null;
		}
		return results.iterator().next();
	}

	@Override
	public IRepresentation loadRepresentation(UUID dbId) {
		IPojo result = loadPojo(dbId);
		if(!(result instanceof IRepresentation)) {
			throw new IllegalArgumentException("Trying to load representation, but dbId " + dbId + " is not of this type");
		}
		return (IRepresentation) result;
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- load: n objects -----------------------------------------
	//--------------------------------------------------------------------------------------------

	/**
	 * return empty list if nothing found
	 * throw IllegalArgumentException with wrong input
	 */
	@Override
	public List<Object> load(String noSql) {
		//TODO implement me!
		throw new NotImplementedException();
	}

	@Override
	public List<IPojo> loadPojos(Class<? extends IPojo> aClass) {
		return db.queryByExample(aClass);
	}

	@Override
	public List<IPojo> loadPojos(final Collection<UUID> dbIds) {
		List<IPojo> result = db.query(new Predicate<IPojo>() {
			private static final long serialVersionUID = 5002820956133637616L;

			public boolean match(IPojo model) {
				return dbIds.contains(model.getDbId());
			}
		});
		if(result.size() != dbIds.size()) {
			throw new IllegalArgumentException("could not retrieve all of " + dbIds + " because at least one was not found.");
		}
		return result;		
	}

	@Override
	public List<IRepresentation> loadRepresentations(DbFilterConfig config) {
		return db.query(QueryBuilder.build(config));
	}

	@Override
	public List<IRepresentation> loadRepresentations(final Collection<UUID> dbIds) {
		List<IRepresentation> result = db.query(new Predicate<IRepresentation>() {
			private static final long serialVersionUID = 6835077782211068285L;

			public boolean match(IRepresentation rep) {
				return dbIds.contains(rep.getDbId());
			}
		});
		if(result.size() != dbIds.size()) {
			throw new IllegalArgumentException("Could not retrieve all of "+ dbIds + " from database.");
		}
		return result;
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- load asynchronously:-------------------------------------
	//--------------------------------------------------------------------------------------------

	@Override
	public void loadAsync(String noSql, final Observer resultHandler) {
		//TODO implement me
		throw new NotImplementedException();
	}

	@Override
	public void loadPojosAsync(final Collection<UUID> dbIds, final Observer resultHandler) {
		if(dbIds.isEmpty()) {
			return;
		}
		db.query(new Predicate<IPojo>() {
			private static final long serialVersionUID = -3798336388318991603L;

			public boolean match(IPojo pojo) {
				boolean match = dbIds.contains(pojo.getDbId());
				if(match) {
					resultHandler.update(null, pojo);
				}
				return false;
			}
		});
	}

	@Override
	public void loadPojosAsync(final Class<? extends IPojo> aClass, final Observer resultHandler) {
		db.query(new Predicate<IPojo>() {
			private static final long serialVersionUID = 3334725227923292475L;

			public boolean match(IPojo pojo) {
				boolean match = pojo.getClass().equals(aClass);
				if(match) {
					resultHandler.update(null, pojo);
				}
				return false;
			}
		});
	}

	@Override
	public void loadRepresentationsAsync(DbFilterConfig config, final Observer resultHandler) {
		db.query(QueryBuilder.build(config, resultHandler));
	}

	@Override
	public void loadRepresentationsAsync(final Collection<UUID> dbIds, final Observer resultHandler) {
		if(dbIds.isEmpty()) {
			return;
		}
		db.query(new Predicate<IRepresentation>() {
			private static final long serialVersionUID = 2009262944294441037L;

			public boolean match(IRepresentation rep) {
				boolean match = dbIds.contains(rep.getDbId());
				if(match) {
					resultHandler.update(null, rep);
				}
				return false;
			}
		});
	}
}

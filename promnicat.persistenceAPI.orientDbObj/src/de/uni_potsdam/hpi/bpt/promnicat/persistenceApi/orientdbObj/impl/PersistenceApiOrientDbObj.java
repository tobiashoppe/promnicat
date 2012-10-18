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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Logger;

import com.orientechnologies.orient.core.command.OCommandExecutor;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.object.ODatabaseObjectTx;
import com.orientechnologies.orient.core.db.record.OTrackedMap;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.exception.OQueryParsingException;
import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.intent.OIntentMassiveRead;
import com.orientechnologies.orient.core.iterator.OObjectIteratorMultiCluster;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.OSQLEngine;
import com.orientechnologies.orient.core.sql.functions.OSQLFunctionAbstract;
import com.orientechnologies.orient.core.sql.query.OSQLAsynchQuery;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.config.OrientDbConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexManager;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.NumberIndex;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.StringIndexStorage;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.util.DbConstants;

/**
 * This is the connection to the database to load, save, delete {@link AbstractModel}, {@link Revision}, 
 * and {@link Representation} and some nearly arbitrary analysis results.
 * The undelying database is OrientDb, a hybrid of graph and document database. The processes 
 * themselves are stored as <code>byte[]</code> though.
 * OrientDb provides 3 layers of access abstraction: <br>
 * - ODatabaseRaw for access on byte level, very fast <br>
 * - ODatabaseDocumentTx for access on JSON level, quite fast, good for inspecting database content <br>
 * - ODatabaseObjectTx for automatic conversion of POJOs, which is a bit slower.<br>
 * All {@link IPojo} are given a database id of form "#5:6" where 5 is the indicator 
 * of the class or cluster and 6 is the 6th object in this cluster. <br>
 * ODatabaseObjectTx can convert many pojos but not all, yet e.g. Map<String, String[]> 
 * for Metadata is not possible yet, see http://code.google.com/p/orient/wiki/Types 
 * on details of what can be converted.
 * <br>
 * All outgoing links of a Pojo are saved and loaded as well (not deleted though).
 * To be able to save a Pojo, all connected classes need to be registered first.
 * In order to increase performance for the very important use case of loading {@link Representation}s, 
 * {@link Representation}s can be loaded as lightweight {@link Representation}s, 
 * which means only the connected {@link Revision} and its {@link AbstractModel} are loaded
 * but no sibling or cousin {@link Representation}s or {@link Revision}s.
 * The {@link Revision} and {@link AbstractModel} need to be loaded to have metadata and title of the process.
 * <br>
 * Loading {@link Representation} and {@link IPojo}s can be synchronous or asynchronous.
 * Synchronous loading collects all results and returns a list of results, which can be a bottleneck in
 * available memory space. Therefore in asynchronous loading, a {@link Observer} is handed one result at 
 * a time which can then be processed and stored or removed before the next result is handled.
 *  
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public class PersistenceApiOrientDbObj implements IPersistenceApi {
	
	private String dbPath = "";
	private String user = "";
	private String password = "";

	private ODatabaseObjectTx db;				// used by OrientDB to access object level
	private NoSqlBuilder noSqlBuilder;	// creates NoSQL commands
	private String fetchplan = "";				// can be used to limit loading depth
	private IndexManager indexMngr = null;		// will remember index names and is stored as singleton in the database
	private final static int memorySize = (int) (Runtime.getRuntime().totalMemory() * 0.8);

	private final static Logger logger = Logger.getLogger(PersistenceApiOrientDbObj.class.getName());
	
	/**
	 * @param configurationFilePath the path to the configuration file (relative to java project)
	 * @return the orientDb accessing the database defined in the file
	 */
	public static PersistenceApiOrientDbObj getInstance(String configurationFilePath) {
		try {
			return (PersistenceApiOrientDbObj) new OrientDbConfigurationParser(configurationFilePath).getDbInstance();
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}
		return null;
	}

	public PersistenceApiOrientDbObj(String dbPath, String user, String password) {
		this.dbPath = dbPath;
		this.user = user;
		this.password = password;
		init();
	}
	
	private void init() {
		openDb();
		noSqlBuilder = new NoSqlBuilder();
		addCustomFunctions();
		fetchplan = "*:-1 " 
				+ DbConstants.ATTR_REVISION + ":1 " //load this connection
				+ DbConstants.ATTR_MODEL + ":1 "
				+ DbConstants.ATTR_REVISIONS + ":0 " //no load, means lazy loading
				+ DbConstants.ATTR_REPRESENTATIONS + ":0";
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#open()
	 */
	@Override
	public void openDb() {
		db = new ODatabaseObjectTx(dbPath); 
		registerPojoClasses();

		if (!db.exists()) {
			db.create();
			initSchema();
			logger.info("Created database at " + dbPath);
		} 
		if (db.isClosed()) {
			db.open(user, password);
			loadIndexMngr();
			logger.info("Opened database at " + dbPath);
		}
		OGlobalConfiguration.FILE_MMAP_MAX_MEMORY.setValue(memorySize);
		OGlobalConfiguration.MEMORY_OPTIMIZE_THRESHOLD.setValue(0.5f); // start garbage collector at 50% heap space
		db.declareIntent(new OIntentMassiveRead());
	}

	/**
	 * This is to tell the database the schema of most used classes. 
	 * Otherwise db calls on an empty database throws Exceptions, if asked for these classes.
	 */
	private void initSchema() {
		//FIXME is their a nicer way to define schema?
		IModel model = new ModelOrientDb();
		db.save(model);
		db.delete(model);
		IRevision rev = new RevisionOrientDb();
		db.save(rev);
		db.delete(rev);
		IRepresentation rep = new RepresentationOrientDb();
		db.save(rep);
		db.delete(rep);
		
		//for StringIndex
		executeCommand("CREATE PROPERTY StringIndexStorage.key STRING");
		//for IndexManager
		createIndexManager();
	}
	
	/**
	 * @return database access on object level to be used for direct database inspection.
	 */
	public ODatabaseObjectTx getInternalDbAccess() {
		return db;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#closeDB()
	 */
	@Override
	public void closeDb() {
		db.close();
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#dropDB()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void dropDb() {
		if (db.exists()) {
			//remove number indices to be able to drop database, sometimes OrientDB needs this
			for(String numberIndexName : (Iterable<String>)indexMngr.getNumberIndices().clone()) {
				@SuppressWarnings("rawtypes")
				NumberIndex nIndex = new NumberIndex(numberIndexName, this);
				nIndex.dropIndex();
			}
			//finally delete db
			db.delete();
			logger.info("Database dropped at " + dbPath);
		} else {
			logger.info("could not drop database, no database found on path " + dbPath);
		}
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#clearCache()
	 */
	@Override
	public void clearCache() { 
		/*
		 * For some use cases, performance can be increased by setting 
		 * db.retainObjects(false), db.getUnderlying.retainRecord(false), 
		 * db.getLevel1Cache().setEnable(false), and db.getLevel2Cache().setEnable(false)
		 * but for other use cases or sequence of use cases, objects might not load or save correctly.
		 * Until this is fixed by OrientDb, only use close() and open()
		 * and rely on Garbage Collection to free old objects.
		 */
		db.getLevel1Cache().clear();
		db.getLevel2Cache().clear();
		if(!db.isClosed()) {
			this.closeDb();
		}
		this.db.open(user, password);
	}

	/**
	 * For initialization, OrientDb needs to know the schema of the main content classes.
	 */
	private void registerPojoClasses() {
		registerPojoClass(ModelOrientDb.class); 
		registerPojoClass(RevisionOrientDb.class); 
		registerPojoClass(RepresentationOrientDb.class);
		registerPojoClass(StringIndexStorage.class);
		registerPojoClass(IndexManager.class);
	}

	/**
	 *  Register a Class before it can be saved at object access level.
	 *  Classes are only stored by their name, without their package. Make sure to have unique names.
	 *  All referenced classes need to be referenced as well, @see {@link #registerPojoPackage(String)}.
	 *  As default, the classes {@link AbstractModel}, {@link Revision}, and {@link Representation} are already registered.
	 * 
	 * @param aClass
	 */
	public void registerPojoClass(Class<?> aClass) {
		db.getEntityManager().registerEntityClass(aClass);
	}
	

	/**
	 * Register all class in this package before they can be saved at object access level.
	 * packagePath is e.g. "de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos"	
	 * or get it via LabelStorage.class.getPackage().getName().
	 * As default, the classes {@link AbstractModel}, {@link Revision}, and {@link Representation} are already registered.
	 * 
	 * @param packagePath
	 */
	public void registerPojoPackage(String packagePath) {
		db.getEntityManager().registerEntityClasses(packagePath);
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#countClass(java.lang.Class)
	 */
	@Override
	public long countClass(Class<? extends IPojo> aClass) {
		return db.countClass(aClass.getSimpleName());
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#executeCommand(java.lang.String)
	 */
	@Override
	public void executeCommand(String sqlCommand) {
		//wrong input throws IllegalArgumentE
		db.command(new OCommandSQL(sqlCommand)).execute();
	}
	
	/*
	 * Checks if this dbId belongs to a Representation by it's appearance.
	 * It does not check, whether this dbId really exists.
	 */
	public boolean isRepresentation(String dbId) {
		ORecordId rid = tryToConvertDbId(dbId);
		return rid.getClusterId() == db.getClusterIdByName(Representation.class.getSimpleName());
	}
	
	/**
	 * @return the singleton index manager 
	 */
	public IndexManager getIndexMngr() {
		return indexMngr;
	}
	
	/**
	 * create a new index manager
	 */
	private void createIndexManager() {
		indexMngr = new IndexManager();
		saveIndexMngr();
	}
	
	
	/**
	 * save the index manager
	 */
	public void saveIndexMngr() {
		db.save(indexMngr);
	}
	
	/**
	 * load the index manager from the database. Assumes that there is only one instance
	 */
	private void loadIndexMngr() {
		OObjectIteratorMultiCluster<Object> result = db.browseClass(IndexManager.class.getSimpleName());
		if(!result.hasNext()) {
			createIndexManager();
		} else {
			indexMngr = (IndexManager) result.next();
		}
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- save and delete: ----------------------------------------
	//--------------------------------------------------------------------------------------------

	/**
	 * Tries to save a pojo into the database.
	 * If this pojo has connections to instances of other classes, call registerPojoClass(Class<?>) 
	 * on each possibly connected class first.
	 * Not all field types are supported by OrientDb, see {@link IPojo} or
	 * http://code.google.com/p/orient/wiki/Types
	 * <br>
	 * Arbitrary Pojos with references to other Pojos need to be registered before saving.
	 * 
	 */
	@Override
	public String savePojo(IPojo pojo) {
		try{
			registerPojoClass(pojo.getClass());
			db.save(pojo); 
			String dbId = db.getIdentity(pojo).toString();
			clearCache(); //remove this object from cache
			return dbId;
		} catch(OSerializationException e) {
			logger.severe("failed to save pojo " + pojo.toString() + 
					"because: \n-- " + e.getMessage() +
					"\n-- make sure to call registerPojoClass() on all possibly referenced and therefore saved classes.");
		} catch(Exception e) {
			logger.severe("failed to save pojo " + pojo.toString() + 
					"because: \n" + e.getMessage());
		} 
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#deletePojos(java.lang.Class)
	 */
	@Override
	public boolean deletePojos(Class<?> aClass) {
		executeCommand("DELETE FROM " + aClass.getSimpleName());
		return true;
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#deletePojos(java.util.Collection)
	 */
	@Override
	public boolean deletePojos(Collection<String> dbIds) {
		for(String dbId : dbIds) {
			tryToConvertDbId(dbId);
		}
		try {
			db.begin(); 
			executeCommand("DELETE FROM " + noSqlBuilder.buildIdList(dbIds));
			db.commit();
			return true;
		} catch (ODatabaseException e) {
			logger.severe("no ids were deleted, because one was not found (" +  e + ")");
			db.rollback();
			return false;
		} catch (OCommandExecutionException e) {
			db.rollback();
			throw new IllegalArgumentException("could not delete ids, because one was not found (" +  e + ")");
		}
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#deletePojo(java.lang.String)
	 */
	@Override
	public boolean deletePojo(String dbId) {
		tryToConvertDbId(dbId);
		try {
			db.begin();
			executeCommand("DELETE FROM [" +  dbId + "]");
			db.commit();
			return true;
		} catch (ODatabaseException e) {
			logger.severe("id was deleted, because it was not found (" +  e + ")");
			db.rollback();
			return false;
		} catch (OCommandExecutionException e) {
			db.rollback();
			throw new IllegalArgumentException("could not delete " + dbId + " because it was not found");
		}
	}


	//--------------------------------------------------------------------------------------------
	//---------------------------------- load: 1 object ------------------------------------------
	//--------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#loadPojo(java.lang.String)
	 */
	@Override
	public IPojo loadPojo(String dbId) {
		tryToConvertDbId(dbId);

		try{
			//FIXME, why does this not load full connections with orientdb?
			//			Object result = db.load(rid);
			//			m = (Model) result;

			//quickfix
			String sql = "SELECT FROM " + dbId;
			List<IPojo> list = loadPojos(sql);
			if(list.isEmpty()) {
				return null;
			}
			return list.get(0);
		} catch (ODatabaseException e) {
			logger.info("Could not retrieve "+ dbId + " from database." + e);
			return null;
		} catch (OCommandExecutionException e) {
			logger.info("Could not retrieve "+ dbId + " from database." + e);
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#loadCompleteModelWithDbId(java.lang.String)
	 */
	@Override
	public IModel loadCompleteModelWithDbId(String dbId) {
		ORecordId rid = tryToConvertDbId(dbId);

		if(!belongsToCluster(rid,  DbConstants.CLS_MODEL)) {
			logger.info("Trying to load model, but dbId " + dbId + " is not of this type");
			return null;
		}

		try{
			clearCache();
			return (IModel) loadPojo(dbId);
		} catch(Exception e) {
			logger.info("Could not retrieve model with dbId "+ dbId + " from database" + e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#loadCompleteModelWithImportedId(java.lang.String)
	 */
	@Override
	public IModel loadCompleteModelWithImportedId(String id) {
		String sql = "SELECT FROM " + DbConstants.CLS_MODEL
				+ " WHERE " +DbConstants.ATTR_IMPORTED_ID + " like '" + id + "'";
		List<IPojo> models = loadPojos(sql);
		if (models.size() > 1){
			throw new IllegalStateException("Model ids must be unique! But, got "
					+ models.size() + "models with id " + id);
		}
		try {
			return (AbstractModel)models.get(0);
		} catch(Exception e) {
//			logger.info("Could not retrieve model with importedId "+ id + " from database" + e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#loadRepresentation(java.lang.String)
	 */
	@Override
	public IRepresentation loadRepresentation(String dbId) {
		ORecordId rid = tryToConvertDbId(dbId);
		if(!belongsToCluster(rid,  DbConstants.CLS_REPRESENTATION)) {
			logger.info("Trying to load representation, but dbId " + dbId + " is not of this type");
			return null;
		}
		try {
			Object result = db.load(rid, fetchplan);
			IRepresentation rep = makeLightweightRepresentation(result);
			return rep;
		} catch(Exception e) {
			logger.info("Could not retrieve representation with dbId "+ dbId + " from database" + e);
		}
		return null;
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- load: n objects -----------------------------------------
	//--------------------------------------------------------------------------------------------

	/**
	 * return empty list if nothing found
	 * throw IllegalArgumentException or OCommandQueryParsingException with wrong input
	 */
	@Override
	public List<Object> load(String noSql) {
		return db.query(new OSQLSynchQuery<Object>(noSql));
	}
	
	private List<IPojo> loadPojos(String noSql) {
		List<IPojo> list = db.query(new OSQLSynchQuery<Object>(noSql));
		return list;
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi#loadPojos(java.lang.Class)
	 */
	@Override
	public List<IPojo> loadPojos(Class<? extends IPojo> aClass) {
		registerPojoClass(aClass);
		// other idea: db.browseClass(aClass.getSimpleName());
		List<IPojo> list = loadPojos("SELECT FROM " + aClass.getSimpleName());
		return list;
	}

	@Override
	public List<IPojo> loadPojos(Collection<String> dbIds) {
		if(dbIds.isEmpty()) {
			return new ArrayList<IPojo>();
		}
		for(String dbId : dbIds) {
			tryToConvertDbId(dbId);
		}
		String sql = noSqlBuilder.build(dbIds);
		try {
			List<IPojo> list = loadPojos(sql);
			return list;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public List<IRepresentation> loadRepresentations(DbFilterConfig config) {
		String nosql = noSqlBuilder.build(config);
		
		List<IRepresentation> reps = db.query(new OSQLSynchQuery<IRepresentation>(nosql).setFetchPlan(fetchplan));
		for(IRepresentation rep : reps) {
			makeLightweightRepresentation(rep);
		}
		return reps;
	}

	@Override
	public List<IRepresentation> loadRepresentations(Collection<String> dbIds) {
		if(dbIds.isEmpty()) {
			return new ArrayList<IRepresentation>();
		}
		for(String dbId : dbIds) {
			tryToConvertDbId(dbId);
		}

		List<IPojo> pojos = null;
		try {
			pojos = loadPojos(dbIds);
		} catch (ODatabaseException e) {
			throw new IllegalArgumentException(e);
		}

		//loaded successfully, create lightweight representations
		List<IRepresentation> reps = new ArrayList<IRepresentation>(pojos.size());
		for(IPojo pojo : pojos) {
			IRepresentation rep = (IRepresentation) pojo;
			makeLightweightRepresentation(rep);
			reps.add(rep);
		}
		return reps;
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- load asynchronously:-------------------------------------
	//--------------------------------------------------------------------------------------------

	@Override
	public void loadAsync(String noSql, final Observer resultHandler) {
		OCommandResultListener listener = new OCommandResultListener() {
			public boolean result(Object iRecord) {
				resultHandler.update(null, iRecord);
				return true;
			}
		};
		executeAsynchQuery(noSql, listener);
	}

	@Override
	public void loadPojosAsync(Collection<String> dbIds, Observer resultHandler) {
		//empty?
		for(String dbId : dbIds) {
			tryToConvertDbId(dbId);
		}
		
		String noSql = noSqlBuilder.build(dbIds);
		loadAsync(noSql, resultHandler);
	}

	@Override
	public void loadPojosAsync(Class<? extends IPojo> aClass, Observer resultHandler) {
		loadAsync("SELECT FROM " + aClass.getSimpleName(), resultHandler);
	}

	@Override
	public void loadRepresentationsAsync(DbFilterConfig config, final Observer resultHandler) {		
		OCommandResultListener listener = new OCommandResultListener() {
			public boolean result(Object doc) {
				IRepresentation rep = new RepresentationOrientDb();
				db.stream2pojo((ODocument)doc, rep, fetchplan);
				resultHandler.update(null, makeLightweightRepresentation(rep));
				return true;
			}
		};
		executeAsynchQuery(noSqlBuilder.build(config), listener);
	}

	@Override
	public void loadRepresentationsAsync(Collection<String> dbIds, final Observer resultHandler) {
		//empty?
		for(String dbId : dbIds) {
			tryToConvertDbId(dbId);
		}

		OCommandResultListener listener = new OCommandResultListener() {
			public boolean result(Object doc) {
				IRepresentation rep = new RepresentationOrientDb();
				db.stream2pojo((ODocument)doc, rep, fetchplan);
				resultHandler.update(null, makeLightweightRepresentation(rep));
				return true;
			}
		};
		
		executeAsynchQuery(noSqlBuilder.build(dbIds), listener);
	}

	//--------------------------------------------------------------------------------------------
	//---------------------------------- private:-------------------------------------------------
	//--------------------------------------------------------------------------------------------


	/**
	 * Used by all asynch queries internally
	 * 
	 * @param noSql
	 * @param listener
	 */
	private void executeAsynchQuery(String noSql, OCommandResultListener listener) {
		try{
			retainObjects(false);
			db.query(new OSQLAsynchQuery<ODocument>(noSql, listener));
		} catch (ODatabaseException e) {
			throw new IllegalArgumentException("could not load all due to: " + e);
		} catch (OQueryParsingException e) {
			throw new IllegalArgumentException("could not load all due to poorly constructed query: " + e);
		} finally {
			retainObjects(true);
		}
	}
	
	/**
	 * Sets the flag whether OrientDB should keep objects in RAM/cache
	 */
	private void retainObjects(boolean retain) {
		db.setRetainObjects(retain);
		db.getUnderlying().setRetainRecords(retain);
	}	
	
	/**
	 * Checks if this database id belongs to the class name.
	 * Database Ids start with the class id up until the # sign.
	 * 
	 * @param rid the representation of a database id
	 * @param className
	 * @return
	 */
	private boolean belongsToCluster(ORecordId rid, String className) {
		return db.getClusterIdByName(className) == rid.getClusterId();
	}

	/**
	 * @param dbId the database id that should be converted to a internal id format if possible
	 */
	private ORecordId tryToConvertDbId(String dbId) {
		try{
			return new ORecordId(dbId);
		} catch(Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Add custom functions that can be used in NoSQL queries
	 */
	private void addCustomFunctions() {	
		
		/*
		 * add function e.g. containsValueSubstring(revision.metadata, [s1,s2,...,sn])
		 * which searches for any occurence of one substring si in the metadata values.
		 */
		OSQLEngine.getInstance().registerFunction("containsValueSubstrings", new OSQLFunctionAbstract("containsValueSubstrings", 2, 2) {
			public String getSyntax() {
				return "containsValueSubstrings(<Map>, <SearchCriteriaList>)";
			}

			@SuppressWarnings("unchecked")
			@Override
			public Object execute(ORecord<?> arg0, Object[] iParameters, OCommandExecutor arg2) {
				if (!(iParameters[0] instanceof OTrackedMap) || !(iParameters[1] instanceof List)) {
					return null;
				}
				OTrackedMap<String> map = (OTrackedMap<String>) iParameters[0];
				List<String> criteria = (List<String>)iParameters[1];
				for(Object criterionO : criteria) {
					//check if at least one criterion is in some map value, or-semantik
					String criterion = criterionO.toString();
					boolean found = false;
					for(String value : map.values()) {
						if(value.contains(criterion)) {
							found = true;
							break;
						}
					}
					if (found) {
						//if one criterion was found, don't check others
						return "true";
					}
				}
				return "true";
			}
		});
	}

	/**
	 * If Representations are loaded, the number of sibling Representations and Revisions can grow huge,
	 * therefore only load the directly connected Revision and Model until the user requests different.
	 * 
	 * @param o the Object that can be cast to a Representation
	 * @return a Representation
	 */
	private IRepresentation makeLightweightRepresentation(Object o) {
		IRepresentation rep = null;
		try {
			rep = (IRepresentation) o;
		} catch (ClassCastException e) {
			return null;
		}

		IRevision rev = rep.getRevision();
		IModel mod = rep.getModel();
		if(rev == null || mod == null) {
			return rep;
		}

		//TODO bottleneck? does orientDb really not load them?
		Set<IRepresentation> representations = new HashSet<IRepresentation>();
		representations.add(rep);
		rev.setAndConnectRepresentations(representations);

		mod.setCompletelyLoaded(false);
		Set<IRevision> revisions = new HashSet<IRevision>();
		revisions.add(rev);
		mod.setAndConnectRevisions(revisions);

		return rep;
	}

	@Override
	public IPojoFactory getPojoFactory() {
		return PojoFactoryOrientDb.init();
	}
}

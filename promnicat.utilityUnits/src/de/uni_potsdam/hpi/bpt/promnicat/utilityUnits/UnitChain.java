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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.ICollectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.DatabaseFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitData;

/**
 * Class containing the appropriate sequence of {@link IUnit}s. Invoking
 * the {@link IUnitChain#execute()} method starts the execution of the {@link IUnitChain}.
 * 
 * @author Tobias Hoppe, Cindy Fähnrich
 * 
 */
public class UnitChain implements IUnitChain<IUnitData<Object>, IUnitData<Object>> {

	/**
	 * Logger used by this class. It is identified by the name of this class.
	 */
	private Logger logger = Logger.getLogger(UnitChain.class.getName());

	/**
	 * Utility unit to start with in the unit chain
	 */
	private IUnit<IUnitData<Object>, IUnitData<Object>> rootUnit = null;

	/**
	 * Actual chain of units
	 */
	private Vector<IUnit<IUnitData<Object>, IUnitData<Object>>> units = new Vector<IUnit<IUnitData<Object>, IUnitData<Object>>>();

	/**
	 * Flag whether to handle errors within the chain(<code>false</code>) or just throw them up(<code>true</code>)
	 */
	private boolean throwErrors = true;
	
	/**
	 * List of all errors occurred during execution
	 */
	private Collection<Exception> errors = new ArrayList<Exception>();
	
	/**
	 * The thread pool used for unit chain execution
	 */
	private ExecutorService threadPool = null;

	private Class<?> unitDataType = null;
	
	/**
	 * Create a new {@link UnitChain} which throws all errors, that occur during execution.
	 * The maximum number of threads used for execution is 4 * number of available cores.
	 * @param unitDataType the type of {@link IUnitData} to be used in the chain. If <code>null</code> is provided, {@link IUnitData} is used.
	 */
	public UnitChain(Class<?> unitDataType){
		this(true, unitDataType);
	}

	/**
	 * Creates a new {@link UnitChain}.
	 * The maximum number of threads used for execution is 4 * number of available cores.
	 * @param throwErrors flag whether to handle errors within the chain(<code>false</code>) or just throw them up(<code>true</code>).
	 * @param unitDataType the type of {@link IUnitData} to be used in the chain. If <code>null</code> is provided, {@link IUnitData} is used.
	 */
	public UnitChain(boolean throwErrors, Class<?> unitDataType) {
		this(throwErrors, -1, unitDataType);
	}
	
	/**
	 * Create a new {@link UnitChain} which throws all errors, that occur during execution.
	 * @param numberOfCores maximum number of threads used for execution. If a number smaller one is provided, the default number of threads is used.
	 * @param unitDataType the type of {@link IUnitData} to be used in the chain. If <code>null</code> is provided, {@link IUnitData} is used.
	 */
	public UnitChain(int numberOfCores, Class<?> unitDataType){
		this(true, numberOfCores, unitDataType);
	}

	/**
	 * Creates a new {@link UnitChain}.
	 * The maximum number of threads used for execution is 4 * number of available cores.
	 * @param throwErrors flag whether to handle errors within the chain(<code>false</code>) or just throw them up(<code>true</code>).
	 * @param numberOfCores maximum number of threads used for execution. If a number smaller one is provided, the default number of threads is used.
	 * @param unitDataType the type of {@link IUnitData} to be used in the chain. If <code>null</code> is provided, {@link IUnitData} is used.
	 */
	public UnitChain(boolean throwErrors, int numberOfCores, Class<?> unitDataType) {
		//less than one thread is not allowed
		if (numberOfCores < 1) {
			numberOfCores = Runtime.getRuntime().availableProcessors() * 4;
		}
		if (unitDataType == null) {
			unitDataType = UnitData.class;
		}  else if (!(UnitData.class.isAssignableFrom(unitDataType))) {
			throw new IllegalArgumentException("Unit data type must be an instance of UnitData!");		
		}
		this.throwErrors = throwErrors;
	
	     this.threadPool = new ThreadPoolExecutor(numberOfCores, numberOfCores,
	                30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));

		
		//this.threadPool = Executors.newFixedThreadPool(numberOfCores);
		this.unitDataType = unitDataType;
	}

	@Override
	public Collection<IUnit<IUnitData<Object>, IUnitData<Object>>> getUnits() {
		return this.units;
	}

	@Override
	public IUnitChain<IUnitData<Object>, IUnitData<Object>> register(
			IUnit<IUnitData<Object>, IUnitData<Object>> unit) {
		if (this.rootUnit == null) {
			this.rootUnit = unit;
		}
		this.units.add(unit);
		return this;
	}

	@Override
	public IUnitChain<IUnitData<Object>, IUnitData<Object>> register(
			Collection<IUnit<IUnitData<Object>, IUnitData<Object>>> units) {
		if (this.rootUnit == null) {
			this.rootUnit = units.iterator().next();
		}
		this.units.addAll(units);
		return this;
	}

	@Override
	public IUnitChain<IUnitData<Object>, IUnitData<Object>> register(
			IUnitChain<IUnitData<Object>, IUnitData<Object>> unitChain) {
		return this.register(unitChain.getUnits());
	}

	@Override
	public Collection<? extends IUnitData<Object> > execute() throws IllegalTypeException, IllegalArgumentException {
		// root unit must be set
		if (this.rootUnit == null) {
			logger.warning("Tried to execute unit chain without units!");
			return null;
		}
		// first unit in chain must be a DataBaseFilterUnit
		if (!(DatabaseFilterUnit.class.isInstance(this.rootUnit))) {
			logger.severe("First unit of a chain must always be of type DataBaseFilterUnit!");
			return null;
		}
		if (!(this.units.lastElement() instanceof ICollectorUnit<?, ?>)) {
			logger.severe("Last unit of a chain must always be of type ICollectorUnit!");
			return null;
		}
		((ICollectorUnit<?, ?>) this.units.lastElement()).reset();
		//chain execution and error handling
		if (!executeChain()) {
			//a serious error occurred
			return null;
		}
		handleOccurredErrors();		
		// further units are executed for each database result within the
		// update-method.
		// finally get the result from the collector unit
		return (Collection<? extends IUnitData<Object>>) ((ICollectorUnit<?, ?>) this.units.lastElement()).getResult();
	}

	/**
	 * execute the current {@link IUnitChain}.
	 * If an error occurred, it is thrown only if error throwing is enabled.
	 * Otherwise, the error is logged.
	 * @return <code>false</code>, if a serious error occurred. Otherwise, <code>true</code>.
	 * @throws IllegalTypeException if a mismatch of {@link IUnit}'s input/output is detected.
	 * @throws IllegalArgumentException if a null pointer was given as {@link IUnit}'s input.
	 */
	
	private boolean executeChain() throws IllegalTypeException,
			IllegalArgumentException {
		try {
			this.rootUnit.execute(new UnitData<Object>(this));
			this.threadPool.shutdown();
			//wait until all unit chains had been processed
			while(!this.threadPool.isTerminated()){
				Thread.yield();
			}
		} catch (IllegalTypeException e) {
			if (this.throwErrors){
				throw e;
			} else {
				logger.severe("An error occured during unit chain execution. The result may be incorrect. Got message:\n" + e);
			}
		} catch (IllegalArgumentException e){
			if (this.throwErrors) {
				throw e;
			} else {
			logger.severe("A serious error occured during chian execution. No result could be generated! Got error message:\n" + e);
			return false;
			}
		}
		return true;
	}

	/**
	 * If any error has been detected and error throwing is enabled, the errors are thrown here.
	 * @throws IllegalTypeException if a mismatch of {@link IUnit}'s input/output is detected.
	 * @throws IllegalArgumentException if a null pointer was given as {@link IUnit}'s input.
	 */
	private void handleOccurredErrors() throws IllegalTypeException,
			IllegalArgumentException {
		//log occurred errors
		if (!this.errors.isEmpty()) {
			for (Exception e : this.errors){
				if (e instanceof IllegalTypeException) {
					throw (IllegalTypeException)e;
				}
				if (e instanceof IllegalArgumentException) {
					throw (IllegalArgumentException)e;
				}
				logger.severe("An unhandeled exception occured. The result may be incorrect! Got error message:\n" + e);
			}
		}
	}

	@Override
	public void update(Observable notifier, Object value) {
		// execute further units for this database result
		// finally synchronized by collector unit
		try{
			this.threadPool.execute(new UnitChainExecutor(this.unitDataType , this.units, this.throwErrors, this.errors, value));
		} catch(RejectedExecutionException e){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				logger.severe(e1.getMessage() + e1.getStackTrace().toString());
			}
			update(notifier, value);
		}
	}

	@Override
	public IUnit<IUnitData<Object>, IUnitData<Object> > getLastUnit() {
		return this.units.lastElement();
	}

	@Override
	public IUnit<IUnitData<Object>, IUnitData<Object> > getFirstUnit() {
		return this.rootUnit;
	}

	@Override
	public String toString() {
		String chainElements = "UNITCHAIN consisting of " + this.units.size()
				+ " Utility Units: \n     ";
		for (IUnit<IUnitData<Object>, IUnitData<Object>> unit : this.units) {
			chainElements += unit.getName() + " --> ";
		}
		chainElements += "\n";
		return chainElements.substring(0, chainElements.lastIndexOf("-->"));
	}
}
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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.pm.FlowNode;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.UnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Test class for {@link UnitChain}.
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class UnitChainTest {

	@Test
	public void testGetUnits(){
		IUnitChain<IUnitData<Object>, IUnitData<Object>> chain = new UnitChain(null);
		chain.register(new MockUnit());
		assertTrue(chain.getUnits().size() == 1);
		
		chain.register(new MockUnit());
		assertTrue(chain.getUnits().size() == 2);
	}
	
	@Test
	public void testGetFirstUnit(){
		IUnitChain<IUnitData<Object>,IUnitData<Object>> chain = new UnitChain(null);	
		chain.register(new MockUnit(FlowNode.class));
		chain.register(new MockUnit());	
		assertTrue(chain.getFirstUnit().getName().equals("ProcessModelFilterUnit"));
	}
	
	@Test
	public void testGetLastUnit(){
		IUnitChain<IUnitData<Object>,IUnitData<Object>> chain = new UnitChain(null);
		chain.register(new MockUnit());	
		chain.register(new MockUnit(FlowNode.class));
		assertTrue(chain.getLastUnit().getName().equals("ProcessModelFilterUnit"));
	}
	
	@Test
	public void testRegister(){
		IUnitChain<IUnitData<Object>,IUnitData<Object>> chain = new UnitChain(null);
		UnitChain smallChain = new UnitChain(null);
		chain.register(new MockUnit());
		assertTrue(chain.getUnits().size() == 1);
		
		chain.register(new MockUnit());
		assertTrue(chain.getUnits().size() == 2);
		
		smallChain.register(new MockUnit());
		smallChain.register(new MockUnit());
		assertTrue(smallChain.getUnits().size() == 2);
		
		chain.register(smallChain);
		assertTrue(chain.getUnits().size() == 4);
		
		
		Collection<IUnit<IUnitData<Object>,IUnitData<Object>>> units = new ArrayList<IUnit<IUnitData<Object>, IUnitData<Object>>>();
		units.add(new MockUnit());
		units.add(new MockUnit());
		chain.register(units);
		assertTrue(chain.getUnits().size() == 6);
	}
	
}

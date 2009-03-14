/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.choiceset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;

/**
 * An AbilityFromClassChoiceSet is a PrimitiveChoiceSet that draws Abilities
 * from a specific PCClass. These Ability objects are previously selected
 * Ability objects; thus AbilityFromClassChoiceSet is intended to be used during
 * object removal from a PlayerCharacter, not object addition.
 * 
 * In particular, AbilityFromClassChoiceSet is designed to handle
 * REMOVE:FEAT|Class.???
 */
public class AbilityFromClassChoiceSet implements
		PrimitiveChoiceSet<AbilitySelection>
{

	/**
	 * The underlying class from which this AbilityFromClassChoiceSet can draw
	 * Abilities
	 */
	private final CDOMSingleRef<PCClass> cl;

	/**
	 * Constructs a new AbilityFromClassChoiceSet which refers to the PCClass
	 * provided in the given CDOMSingleRef.
	 * 
	 * @param pcc
	 *            A reference to the PCClass this AbilityFromClassChoiceSet can
	 *            draw Abilities from
	 */
	public AbilityFromClassChoiceSet(CDOMSingleRef<PCClass> pcc)
	{
		cl = pcc;
	}

	/**
	 * Returns a representation of this AbilityFromClassChoiceSet, suitable for
	 * storing in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 * @return A representation of this AbilityFromClassChoiceSet, suitable for
	 *         storing in an LST file.
	 */
	public String getLSTformat(boolean useAny)
	{
		return "CLASS." + cl.getLSTformat();
	}

	/**
	 * Returns true if this AbilityFromClassChoiceSet is equal to the given
	 * Object. Equality is defined as being another AbilityFromClassChoiceSet
	 * object with equal Class represented by the reference provided during
	 * construction of the AbilityFromClassChoiceSets
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AbilityFromClassChoiceSet)
		{
			AbilityFromClassChoiceSet other = (AbilityFromClassChoiceSet) o;
			return cl.equals(other.cl);
		}
		return false;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * AbilityFromClassChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return cl.hashCode();
	}

	/**
	 * Returns the Class contained within this AbilityFromClassChoiceSet
	 * 
	 * @return the Class contained within this AbilityFromClassChoiceSet
	 */
	public Class<? super AbilitySelection> getChoiceClass()
	{
		return AbilitySelection.class;
	}

	/**
	 * Returns a Set containing the Objects which this AbilityFromClassChoiceSet
	 * contains.
	 * 
	 * This method is value-semantic, meaning that ownership of the Set returned
	 * by this method will be transferred to the calling object. Modification of
	 * the returned Set will not modify the AbilityFromClassChoiceSet, and
	 * modifying the AbilityFromClassChoiceSet after the Set is returned will
	 * not modify the Set.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this
	 *            AbilityFromClassChoiceSet should be returned.
	 * @return A Set containing the Objects which this AbilityFromClassChoiceSet
	 *         contains.
	 */
	public Set<AbilitySelection> getSet(PlayerCharacter pc)
	{
		PCClass aClass = pc.getClassKeyed(cl.resolvesTo().getKeyName());
		Set<AbilitySelection> set = new HashSet<AbilitySelection>();
		if (aClass != null)
		{
			for (PCLevelInfo element : pc.getLevelInfo())
			{
				if (element.getClassKeyName().equalsIgnoreCase(
						aClass.getKeyName()))
				{
					for (Ability aFeat : (List<Ability>) element.getObjects())
					{
						set.add(new AbilitySelection(aFeat, aFeat
								.getAbilityNature()));
					}
				}
			}
			List<Ability> abilityList = pc.getAssocList(aClass,
					AssociationListKey.ADDED_FEAT);
			if (abilityList != null)
			{
				for (Ability aFeat : abilityList)
				{
					set.add(new AbilitySelection(aFeat, aFeat
							.getAbilityNature()));
				}
			}
			for (int lvl = 0; lvl < aClass.getLevel(); lvl++)
			{
				PCClassLevel pcl = aClass.getClassLevel(lvl);
				abilityList = pc.getAssocList(pcl,
						AssociationListKey.ADDED_FEAT);
				if (abilityList != null)
				{
					for (Ability aFeat : abilityList)
					{
						set.add(new AbilitySelection(aFeat, aFeat
								.getAbilityNature()));
					}
				}
			}
		}
		return null;
	}
}

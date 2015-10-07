package org.opencloudengine.garuda.beluga.utils.classloader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

public class CompoundEnumeration<E> implements Enumeration<E> {

	private List<Enumeration<E>> enumerationList;
	private int index;

	public CompoundEnumeration() {
		enumerationList = new ArrayList<Enumeration<E>>();
	}

	public void add(Enumeration<E> e) {
		enumerationList.add(e);
	}

	@Override
	public boolean hasMoreElements() {
		while (index < enumerationList.size()) {
			if (enumerationList.get(index) != null && enumerationList.get(index).hasMoreElements()) {
				return true;
			}
			index++;
		}
		return false;
	}

	@Override
	public E nextElement() {
		if (!hasMoreElements()) {
			throw new NoSuchElementException();
		}
		return (E) enumerationList.get(index).nextElement();
	}

}

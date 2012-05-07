package com.factual.base;

import java.io.Serializable;
import java.util.List;

public class CategoryGroup implements Serializable, Comparable<CategoryGroup> {
	private static final long serialVersionUID = 1L;
	private String categoryName;
	private List<Shop> shops;

	public String getCategoryName() {
		return categoryName;
	}
	
	public List<Shop> getShops() {
		return shops;
	}
	
	public CategoryGroup(String categoryName, List<Shop> shops) {
		super();
		this.categoryName = categoryName;
		this.shops = shops;
	}
	
	public int compareTo(CategoryGroup other) {
		return new Integer(other.getShops().size()).compareTo(shops.size());
	}
}

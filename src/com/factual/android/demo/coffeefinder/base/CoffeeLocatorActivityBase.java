package com.factual.android.demo.coffeefinder.base;

import android.os.Bundle;

import com.factual.driver.Query;
import com.factual.android.demo.base.BusinessSearchActivity;

public class CoffeeLocatorActivityBase extends BusinessSearchActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isCategoryFilterPresent() {
    	return true;
    }

    @Override
	protected Query applyPreFilter(Query query) {
    	return query.or(query.field("category").equal("Food & Beverage > Cafes, Coffee Houses & Tea Houses"), query.field("name").beginsWith("Dunkin Donuts"));
	}

    @Override
	protected String getAdBannerId() {
		return "a14f3376af22b14";
	}

}
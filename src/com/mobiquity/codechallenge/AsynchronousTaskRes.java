package com.mobiquity.codechallenge;

import java.util.ArrayList;

//Interface implemented to get result back from AsyncTask to Activity
public interface AsynchronousTaskRes {
	void postResult(ArrayList<String> result);
}

package org.hxzon.util.json;

//Copyright 2007, 2010 The Apache Software Foundation
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

//package org.apache.tapestry5.json;

/**
* An interface that allows an object to be stored as a {@link JSONObject} or {@link JSONArray} value.
* When printed, the value of {@link #toJSONString()} is printed without quotes or other substitution; it
* is the responsibility of the object to provide proper JSON output.
*/
public interface JSONString {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON serialization.
     * 
     * @return A strictly syntactically correct JSON text.
     */
    public String toJSONString();
}

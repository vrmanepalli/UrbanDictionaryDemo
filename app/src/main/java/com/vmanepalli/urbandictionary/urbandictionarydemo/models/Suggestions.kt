package com.vmanepalli.urbandictionary.urbandictionarydemo.models

import java.io.Serializable

/**
 * @author vmanepalli
 * Date: 2020-06-05
 * Time: 10:22
 */
data class Suggestions(val word:String, val definition:String, val defid:Long): Serializable
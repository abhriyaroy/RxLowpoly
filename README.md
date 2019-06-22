<p align="center"><img src="https://i.imgur.com/0q5xcQ0.jpg"></p>

# LowpolyRx

 An android library to convert your dull normal images into awesome ones with a crystallized lowpoly effect.
 <br>

## Table of Contents
 - [Introduction](#introduction)
 - [Samples](#samples)
 - [Library Details](#library-details)
 - [Installation](#installation)
 - [Usage Examples](#usage-examples)
 - [How to Contribute](#how-to-contribute)
 - [About the Author](#about-the-author)
 - [License](#license)

## Introduction

 LowpolyRxJava serves as an improvement over this [repository](https://github.com/xyzxqs/XLowPoly) by 
  - fixing `out of memory` crashes by scaling down the image losslessly before processing 
  - providing better quality results by using `8000` as the point count by default.
  - provides wider choice of input sources like `uri`, `bitmap`, `file path` or `drawable resource`.
  - natively using `RxJava` for background processing thereby reducing boilerplate code on the developer's end.

## Samples

 Original Image | Lowpoly Image
 -------------- | -------------
 <img src="https://i.imgur.com/mHZhqia.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/Z4zOgqH.jpg"  alt="Lowpoly" width=400 height=250>
 <img src="https://i.imgur.com/C5wzAqx.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/mLjjrax.jpg"  alt="Lowpoly" width=400 height=250>
 <img src="https://i.imgur.com/Ho86fyo.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/pm8MV8m.png"  alt="Lowpoly" width=400 height=250>
 <img src="https://i.imgur.com/D4DP8fu.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/1zgjCyE.jpg"  alt="Lowpoly" width=400 height=250>
							  
## App Details

 - LowpolyRxJava uses [JNI](#jni) with 64 bit support to meet google specified requirement for all apps to be 64 bit enabled by August      2019.
 - Use of [JNI](#jni) enables much faster execution than other similar libraries.
 - Use of [Sobel Operator](#sobel-operator) for edge detection.
 - Use of [Delaunay Triangulation](#delaunay-triangulation) on the result from the sobel operator to construct the final crsystallized      lowpoly effect on the image. 
 
 ### JNI
 
  LowpolyRx uses the <a href="https://developer.android.com/training/articles/perf-jni">Java Native Interface</a> to use native code written in `C` which provides much faster processing for `edge detection` using the [Sobel Operator](#sobel-operator) and then implementing the [Delaunay Triangulation](#delaunay-triangulation) algorithm.
 
 ### Sobel Operator
 
 The <a href="http://homepages.inf.ed.ac.uk/rbf/HIPR2/sobel.htm">Sobel Edge Detector</a> is a gradient based edge detection algorithm which provides us with seperate planes on which the [Delaunay Triangulation](#delaunay-triangulation) can be applied.
 
 ### Delaunay Triangulation

  We take a set P of discrete points on an image plane P and apply <a href="https://en.wikipedia.org/wiki/Delaunay_triangulation">Delaunay Triangulation</a> DT(P) to produce triangles connecting 3 points   at a time such that no point in P is inside the circumcircle of any triangle in DT(P). These separate triangles taken together in-turn   provide us with the image having a crystallized effect.
 
  <p align="center"><img src="https://i.imgur.com/MpOuHuw.png" width=330 height=300></p>
 
  Which leads to the resultant crystallized image as :- <br>
 
  <p align="center"><img src="https://i.imgur.com/V1OPCPJ.png" width=250 height=250></p>
 
## Installation

 Step 1. Add the JitPack repository to your project :

 Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  Step 2. Add the dependency in your app module's build.gradle file

	dependencies {
		...
	        implementation 'com.github.abhriyaroy:LowpolyRxJava:1.0.1'
	}

 That's it! <br>

  Please note that using this library, it is assumed that RxJava and RxAndroid are already added as dependencies in your project but in-   case you don't have these dependencies, please add the following dependencies to your app module's build.gradle file :-
	
	dependencies{
		...
		// Rx java
  		implementation "io.reactivex.rxjava2:rxjava:$LATEST_RX_JAVA_VERSION"
  		// Rx android
  		implementation "io.reactivex.rxjava2:rxandroid:$LATEST_RX_ANDROID_VERSION"
	}

## Usage Examples

### Kotlin way - <br>

Using `uri` :-

	LowPolyRx.generateLowpoly(context, uri)
		 // Observe on thread according to your need
      	.observeOn(AndroidSchedulers.mainThread())
		.subscribe({bitmap ->
			// Do something with the result bitmap
		},{
			// Show some error message
		})
		
Or using `bitmap` :-

	LowPolyRx.generateLowpoly(originalBitmap)
		 // Observe on thread according to your need
      	.observeOn(AndroidSchedulers.mainThread())
		.subscribe({bitmap ->
			// Do something with the result bitmap
		},{
			// Show some error message
		})
		
 Or using `drawable` :-

	LowPolyRx.generateLowpoly(context, R.drawable.image)
        	// Observe on thread according to your need
        	.observeOn(AndroidSchedulers.mainThread())
        	.subscribe({bitmap ->
         		 // Do something with the result bitmap
        	},{
          		// Show some error message
        	})
		
 Or using `file path` :-

	LowPolyRx.generateLowpoly(filePath)
        	// Observe on thread according to your need
        	.observeOn(AndroidSchedulers.mainThread())
        	.subscribe({bitmap ->
          		// Do something with the result bitmap
        	},{
         		 // Show some error message
        	})

	
### Java way - <br>

Using `uri` :-

	LowPolyRx.generateLowpoly(context, uri)
	    	 // Observe on thread according to your need
      	.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Consumer<Bitmap>() {
          		@Override public void accept(Bitmap bitmap) {
				// Do something with the result bitmap
				
          		}
        	}, new Consumer<Throwable>() {
          		@Override public void accept(Throwable throwable) {
            			// Show some error message
					
          		}
        	});
		
Or using `bitmap` :-
  
  	 LowPolyRx.generateLowpoly(originalBitmap)
	    	 // Observe on thread according to your need
      	.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Consumer<Bitmap>() {
          		@Override public void accept(Bitmap bitmap) {
				// Do something with the result bitmap
				
          		}
        	}, new Consumer<Throwable>() {
          		@Override public void accept(Throwable throwable) {
            			// Show some error message
					
          		}
        	});
			
 Or using `drawable` :-

	LowPolyRx.generateLowpoly(context, R.drawable.image)
	    	// Observe on thread according to your need
      	.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Consumer<Bitmap>() {
          		@Override public void accept(Bitmap bitmap) {
				// Do something with the result bitmap
			
          		}
        	}, new Consumer<Throwable>() {
          		@Override public void accept(Throwable throwable) {
            			// Show some error message
				
          		}
        	});
		
 Or using `file path` :-

	LowPolyRx.generateLowpoly(filePath)
	    	// Observe on thread according to your need
      	.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Consumer<Bitmap>() {
          		@Override public void accept(Bitmap bitmap) {
				// Do something with the result bitmap
					
          		}
        	}, new Consumer<Throwable>() {
          		@Override public void accept(Throwable throwable) {
            			// Show some error message
				
          		}
        	});
	
 <br>

  You can additionally supply `pointCount` as an optional float argument to each of the above methods depending on your needs. The         default is `pointCount = 8000f`<br>

  A full implementation can be found in the app module of this repository.

## How to Contribute

  Please feel free to raise an issue in-case you come across a bug or even if you have any minor suggestion.

## About the Author
### Abhriya Roy

 Android Developer with 2 years of experience in building apps that look and feel great. 
 Enthusiastic towards writing clean and maintainable code.
 Open source contributor.

 <a href="https://www.linkedin.com/in/abhriya-roy/"><img src="https://i.imgur.com/toWXOAd.png" alt="LinkedIn" width=40 height=40></a>     &nbsp;
 <a href="https://twitter.com/AbhriyaR"><img src="https://i.imgur.com/ymEo5Iy.png" alt="Twitter" width=42 height=40></a> 
 &nbsp;
 <a href="https://stackoverflow.com/users/6197251/abhriya-roy"><img src="https://i.imgur.com/JakJaHP.png" alt="Stack Overflow" width=40  height=40></a> 
 &nbsp;
 <a href="https://angel.co/abhriya-roy?public_profile=1"><img src="https://i.imgur.com/TiwMDMK.pngg" alt="Angel List" width=40  height=40></a>

 <br>

## License

    Copyright 2019 Abhriya Roy

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

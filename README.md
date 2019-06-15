<p align="center"><img src="https://i.imgur.com/0q5xcQ0.jpg" width=750 height=450></p>

# LowpolyRxJava Android

An android library to convert your dull normal images into awesome ones with a crystallized lowpoly effect.
<br>

## Table of Contents
- [Introduction](#introduction)
- [Samples](#samples)
- [Insights](#insights)
- [Installation](#installation)
- [Usage Examples](#usage-examples)
- [How to Contribute](#how-to-contribute)

## Introduction
LowpolyRxJava serves as an improvement over this <a href="https://github.com/xyzxqs/XLowPoly">repository</a> by 
 - providing better quality results.
 - provides wider choice of input sources like file path, bitmap or drawable resource.
 - natively using RxJava for background processing thereby reducing boilerplate code on the developer's end.

## Samples

<p align="center>
  <img src="https://i.imgur.com/mHZhqia.jpg" alt="Original" width=400 height=300>
  <img src="https://i.imgur.com/Z4zOgqH.jpg" alt="Lowpoly" width=400 height=300>
</p>
							  
<p align="center>
  <img src="https://i.imgur.com/C5wzAqx.jpg" alt="Original" width=400 height=300>
  <img src="https://i.imgur.com/mLjjrax.jpg" alt="Lowpoly" width=400 height=300>
</p>
							  
<p align="center>
  <img src="https://i.imgur.com/Ho86fyo.jpg" alt="Original" width=400 height=300>
  <img src="https://i.imgur.com/pm8MV8m.png" alt="Lowpoly" width=400 height=300>
</p>
							  
<p align="center>
  <img src="app/src/main/res/mipmap-xxxhdpi/sample4.jpeg" alt="Original" width=400 height=300>
  <img src="https://i.imgur.com/3uYLnKI.jpg" alt="Lowpoly" width=400 height=300>
</p>
							  
<p align="center>
  <img src="https://i.imgur.com/D4DP8fu.jpg" alt="Original" width=400 height=300>
  <img src="https://i.imgur.com/1zgjCyE.jpg" width=400 height=300>
</p>

## Insights

 - LowpolyRxJava uses [JNI](#jni) with 64 bit support to meet google specified requirement for all apps to be 64 bit enabled by August 2019.
 - Use of [JNI](#jni) enables much faster execution than other similar libraries.
 - Use of [Sobel Operator](#sobel-operator) for edge deteaction.
 - Use of [Delaunay Triangulation](#delaunay-triangulation) on the result from the sobel operator to construct the final crsystallized lowpoly effect on the image. 
 
 ### JNI
 
Java is the default programming language to make applications on Android. However, Java is not always the best solution for making fast apps. Here comes the Java Native Interface (JNI) which defines a way for the bytecode that Android compiles from managed code (written in Java or Kotlin programming languages) to interact with native code (written in C/C++) which is many times faster than the compiled Java/Kotlin code. Thus, to let developers to make optimized part of codes in C/C++, Google offers the Android Native Development Kit (NDK) which allows developers to write code in C/C++ that compiles to native code.<br>
LowpolyRx uses native code for edge detection using the [Sobel Operator](#sobel-operator) and also for implementing the [Delaunay Triangulation](#delaunay-triangulation) algorithm.
 
 
 ### Sobel Operator
 
Detecting edges is one of the fundamental operations you can do in image processing. It helps you reduce the amount of data (pixels) to process and maintains the "structural" aspect of the image. The Sobel edge detector is one such gradient based method. It works with first order derivatives. It calculates the first derivatives of the image separately for the X and Y axes. The derivatives are only approximations (because the images are not continuous). To approximate them, the following kernels are used for convolution: <br>

<p align="center"><img src="https://i.imgur.com/p52Cs6s.png" width=500 height=250></p>
 
 For further understanding please refer to http://homepages.inf.ed.ac.uk/rbf/HIPR2/sobel.htm
 
 ### Delaunay Triangulation
 
 The triangulation algorithm is named after Boris Delaunay for his work on this topic from 1934.<br>
 We take a set P of discrete points in an image plane P and apply Delaunay Triangulation DT(P) to produce traingles connecting 3 points at a time such that no point in P is inside the circumcircle of any triangle in DT(P). These seperate triangles taken togeteher inturn provide us with the image having a crystallized effect.
 
 <p align="center"><img src="https://i.imgur.com/MpOuHuw.png" width=330 height=300></p>
 
 Which leads to the resultant crystallized image as :- <br>
 
 <p align="center"><img src="https://i.imgur.com/V1OPCPJ.png" width=250 height=250></p>
 <p align="center">Credits: <a href="https://en.wikipedia.org/wiki/Delaunay_triangulation">Wikipedia</a></p>
 
 
## Installation

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  Step 2. Add the dependency

	dependencies {
		...
	        implementation 'com.github.abhriyaroy:LowpolyRxJava:1.0.1'
	}

That's it!! <br>

Please note that using this library, it is assumed that RxJava and RxAndroid are already added as dependencies in your project but incase, you don't have these dependencies, please add the following dependencies to your `app/build.gradle` file :-
	
	dependencies{
		...
		// Rx java
  		implementation "io.reactivex.rxjava2:rxjava:$LATEST_RX_JAVA_VERSION"
  		// Rx android
  		implementation "io.reactivex.rxjava2:rxandroid:$LATEST_RX_ANDROID_VERSION"
	}

## Usage Examples

### Kotlin way - <br>

	LowPolyRx().getLowPolyImage(originalBitmap)
		 // Observe on thread according to your need
      		.observeOn(AndroidSchedulers.mainThread())
		.subscribe({bitmap ->
			// Do something with the result bitmap
		},{
			// Show some error message
		})
		
Or

	LowPolyRx().getLowPolyImage(this, R.drawable.image)
        	// Observe on thread according to your need
        	.observeOn(AndroidSchedulers.mainThread())
        	.subscribe({bitmap ->
         		 // Do something with the result bitmap
        	},{
          		// Show some error message
        	})
		
Or

	LowPolyRx().getLowPolyImage(filePath)
        	// Observe on thread according to your need
        	.observeOn(AndroidSchedulers.mainThread())
        	.subscribe({bitmap ->
          		// Do something with the result bitmap
        	},{
         		 // Show some error message
        	})

	
### Java way - <br>
  
  	 new LowPolyRx().getLowPolyImage(originalBitmap)
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
			
Or

	new LowPolyRx().getLowPolyImage(context, R.drawable.image)
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
		
Or

	new LowPolyRx().getLowPolyImage(filePath)
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

You can additionally supply `pointCount` as an optional float argument to each of the above methods depending on your needs. The default is `pointCount = 100f`<br>

A full implementation can be found in the app module of this repository.

## How to Contribute

Please feel free to raise an issue incase you come across a bug or even if you have any minor suggestion. Also please raise a Pull Request if you've made any improvements which you feel should be incorporated into this library.

## About the Author
### Abhriya Roy

Android Developer with 2 years of experience in building apps that look and feel great. 
Enthusiastic towards writing clean and maintainable code.
Open source contributor.

<a href="https://www.linkedin.com/in/abhriya-roy/"><img src="https://i.imgur.com/toWXOAd.png" alt="LinkedIn" width=40 height=40></a> &nbsp;
<a href="https://twitter.com/AbhriyaR"><img src="https://i.imgur.com/ymEo5Iy.png" alt="Twitter" width=42 height=40></a> 
&nbsp;
<a href="https://stackoverflow.com/users/6197251/abhriya-roy"><img src="https://i.imgur.com/JakJaHP.png" alt="Stack Overflow" width=40 height=40></a> 
&nbsp;
<a href="https://angel.co/abhriya-roy?public_profile=1"><img src="https://i.imgur.com/TiwMDMK.pngg" alt="Angel List" width=40 height=40></a>

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

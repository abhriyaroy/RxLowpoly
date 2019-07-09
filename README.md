<p align="center"><img src="https://i.imgur.com/mtMoSDX.jpg"></p>

# RxLowpoly

 An android library to convert your dull ordinary images into awesome
 ones with a crystallized lowpoly effect. <br>

## Table of Contents
 - [Introduction](#introduction)
 - [Lowpoly Samples](#lowpoly-samples)
 - [Library Details](#library-details)
 - [Installation](#installation)
 - [Usage Examples](#usage-examples)
 - [Critical Analysis](#critical-analysis)
 - [Sample App](#sample-app)
 - [How to Contribute](#how-to-contribute)
 - [About the Author](#about-the-author)
 - [License](#license)

## Introduction

 LowpolyRxJava serves as an improvement over [XLowPoly](https://github.com/xyzxqs/XLowPoly) by 
 -  fixing `out of memory` crashes by scaling down the image in a
    loss-less manner before processing.
 -  providing better quality results by using `4000` as the point count
    by default which provides a good trade-off between speed and time.
 -  the higher point count leads to a longer execution period, but it is
    significantly reduced by `scaling down the image` before processing.
 -  provides wider choice of input sources like `bitmap`, `file`, `uri`
    or `drawable resource`.
 -  natively using `RxJava` for background processing thereby reducing
    boilerplate code on the developer's end.

## Lowpoly Samples

 Original Image | Lowpoly Image
 -------------- | -------------
 <img src="https://i.imgur.com/iCddK29.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/Z4zOgqH.jpg"  alt="Lowpoly" width=400 height=250>
 <img src="https://i.imgur.com/C5wzAqx.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/mLjjrax.jpg"  alt="Lowpoly" width=400 height=250>
 <img src="https://i.imgur.com/Ho86fyo.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/pm8MV8m.png"  alt="Lowpoly" width=400 height=250>
 <img src="https://i.imgur.com/D4DP8fu.jpg" alt="Original" width=400 height=250> | <img src="https://i.imgur.com/1zgjCyE.jpg"  alt="Lowpoly" width=400 height=250>
							  
## Library Details

 - LowpolyRxJava uses [JNI](#jni) with 64 bit support to meet google
   specified requirement for all apps to be 64 bit enabled by August
   2019.
 - Use of [JNI](#jni) enables much faster execution than other similar libraries.
 - Use of [Sobel Operator](#sobel-operator) for edge detection.
-  Use of [Delaunay Triangulation](#delaunay-triangulation) on the
   result from the sobel operator to construct the final crystallized
   lowpoly effect on the image.
 
 ### JNI
 
  LowpolyRx uses the <a href="https://developer.android.com/training/articles/perf-jni">Java Native Interface</a> to use native code written in `C` which provides much faster processing for `edge detection` using the [Sobel Operator](#sobel-operator) and then implementing the [Delaunay Triangulation](#delaunay-triangulation) algorithm.
 
 ### Sobel Operator
 
 The <a href="http://homepages.inf.ed.ac.uk/rbf/HIPR2/sobel.htm">Sobel Edge Detector</a> is a gradient based edge detection algorithm which provides us with seperate planes on which the [Delaunay Triangulation](#delaunay-triangulation) can be applied.
 
 ### Delaunay Triangulation

  We take a set P of `discrete points` on an image plane P and apply <a
  href="https://en.wikipedia.org/wiki/Delaunay_triangulation">Delaunay
  Triangulation</a> DT(P) to produce `triangles` connecting 3 points at
  a time such that no point in P is inside the `circum-circle` of any
  `triangle` in DT(P). These separate triangles taken together in-turn
  provide us with the image having a `crystallized` effect.
 
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

  Please note that using this library, it is assumed that RxJava and RxAndroid are already added as dependencies in your project but in-case you don't have these dependencies, please add the following dependencies to your app module's build.gradle file :-
	
	dependencies{
		...
		// Rx java
  		implementation "io.reactivex.rxjava2:rxjava:$LATEST_RX_JAVA_VERSION"
  		// Rx android
  		implementation "io.reactivex.rxjava2:rxandroid:$LATEST_RX_ANDROID_VERSION"
	}

## Usage Examples

### Asynchronous call 

- The most simple use case is :-

        RxLowpoly.with(context)
            .input(bitmap) 
            .generateAsync()
    
  Along with `Bitmaps` we can also use `Drawables` or `Files` or `Uri`
  as input.

 - When we need to downscale the image :-

        RxLowpoly.with(context)
            .input(bitmap)
            .overrideScaling(downScalingFactor)
            .generateAsync()
	
 - When we need to set a maximum width for the image :-
	
        RxLowpoly.with(context)
            .input(bitmap)
            .overrideScaling(maxWidth)
            .generateAsync()
	
 - We can also set a quality for the lowpoly image :- 

        RxLowpoly.with(context)
            .input(inputUri)
            .overrideScaling(downScalingFactor)
            .quality(Quality.HIGH)
            .generateAsync()
	
   `VERY_HIGH`, `MEDIUM`, `LOW` and `VERY_LOW` are also available as
   Quality configurations
	
-  To save the lowpoly image to a file :-

        RxLowpoly.with(context)
            .input(inputUri)
            .overrideScaling(downScalingFactor)
            .quality(Quality.HIGH)
            .output(outputFile) // An uri of a file is also supported as an output destination
            .generateAsync()
	
All  `asynchronous` operation is done on the `io scheduler`.

### Synchronous call 

Replacing `generateAsync()` with `generate()` in each of the [Asynchronous call](#asynchronous-call) examples leads to a `synchronous call` with a lowpoly `bitmap` as a result.
	
A `bitmap` of the generated lowpoly image is always returned irrespective of `synchronous` or `asynchronous` calls and whether an output `file` or `uri` is supplied using the `output` method.<br>

  Note : A full implementation can be found in the <a href="https://github.com/abhriyaroy/LowpolyRx/tree/master/app">app module</a> of this repository or in the open sourced <a href="https://github.com/abhriyaroy/WallR2.0">WallR</a> app.
  
## Critical Analysis

The following tests have been performed on a `Xiaomi Redmi Note 5 Pro with 6 gb Ram`. <br>

 
Original Image &nbsp; &nbsp; &nbsp; | Lowpoly Image | Input Source | Output Type | Quality | Execution Time (ms)
----------------------------------- | ------------- | ------------ | ----------- | ------- | ------
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/AR10HF3.png" width=360 height=200> | Bitmap | File | Very High | 15813
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/XDd5nti.png" width=360 height=200> | Drawable | File | Very High | 16275
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/MVHzIbs.png" width=360 height=200> | File | File | Very High | 15987
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/cajVv2j.png" width=360 height=200> | Uri | File | Very High | 15931
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/OMSV6Ks.png" width=360 height=200> | Bitmap | File | High | 4547
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/9sThhqj.png" width=360 height=200> | Drawable | File | High | 5088
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/mlSfBBD.png" width=360 height=200> | File | File | High | 4734
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/HvBU1en.png" width=360 height=200> | Uri | File | High | 4612
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/KDMzSwy.png" width=360 height=200> | Bitmap | File | Medium | 1113
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/wz1aAhd.png" width=360 height=200> | Drawable | File | Medium | 1672
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/20YfeVF.png" width=360 height=200> | File | File | Medium | 1297
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/7tYMYcZ.png" width=360 height=200> | Uri | File | Medium | 1152
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/TeYASKY.png" width=360 height=200> | Bitmap | File | Low | 918
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/BXUCBJB.png" width=360 height=200> | Drawable | File | Low | 1496
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/FXmsHa5.png" width=360 height=200> | File | File | Low | 1091
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/muLCMQX.png" width=360 height=200> | Uri | File | Low | 996
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/R3p7FOn.png" width=360 height=200> | Bitmap | File | Very Low | 850
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/lei9Yzx.png" width=360 height=200> | Drawable | File | Very Low | 1024
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/RyvOjuF.png" width=360 height=200> | File | File | Very Low | 923
<img src="https://i.imgur.com/iCddK29.jpg" width=360 height=200> | <img src="https://i.imgur.com/cKZgHvM.png" width=360 height=200> | Uri | File | Very Low | 876



Thus it is evident that when quality is set to `High`, it provides a good trade-off between speed and texture hence the default value of `Quality` is set to `HIGH`.<br>
Also, we notice that `bitmap` is the fastest input format followed by `Uri`, `File` and `Drawable` respectively.

## Sample App

The <a href="https://play.google.com/store/apps/details?id=com.zebrostudio.lowpolyrx">sample app</a> provides an implementation of various configurations of `RxLowpoly` which one can experience and assess first hand before using the library.
-  Screenshots :-

<p>
<img src="https://i.imgur.com/iAfVx56.png" width=250 height=500> &nbsp;&nbsp;
<img src="https://i.imgur.com/XPtYUZL.png" width=250 height=500> &nbsp;&nbsp;
<img src="https://i.imgur.com/2gLp46d.png" width=250 height=500> &nbsp;&nbsp;
<img src="https://i.imgur.com/GqQZGwg.png" width=250 height=500> &nbsp;&nbsp;
</p>


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
 <a href="https://angel.co/abhriya-roy?public_profile=1"><img src="https://i.imgur.com/TiwMDMK.png" alt="Angel List" width=40  height=40></a>
 &nbsp;
 <a href="https://play.google.com/store/apps/developer?id=Zebro+Studio"><img src="https://i.imgur.com/Rj1IsYI.png" alt="Angel List" width=40  height=40></a>

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

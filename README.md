# LowpolyRxJava Android

An android library to convert your dull normal images into awesome ones with a crystallized lowpoly effect.
<br>
This is a feature of the WallR android app. Check it out at - https://play.google.com/store/apps/details?id=zebrostudio.wallr100&hl=en

## Samples

<div>
  <img src="app/src/main/res/drawable/sample1.jpeg" alt="Original" width=400 height=300>
  <img src="Outputs/output1.jpeg" alt="Lowpoly" width=400 height=300>
</div>
<br>
<div>
  <img src="app/src/main/res/drawable/sample2.jpeg" alt="Original" width=400 height=300>
  <img src="Outputs/output2.jpeg" alt="Lowpoly" width=400 height=300>
</div>
<br>
<div>
  <img src="app/src/main/res/drawable/sample3.jpeg" alt="Original" width=400 height=300>
  <img src="Outputs/output3.jpeg" alt="Lowpoly" width=400 height=300>
</div>
<br>
<div>
  <img src="app/src/main/res/drawable/sample4.jpeg" alt="Original" width=400 height=300>
  <img src="Outputs/output4.jpeg" alt="Lowpoly" width=400 height=300>
</div>
<br>
<div>
  <img src="app/src/main/res/drawable/sample5.jpeg" alt="Original" width=400 height=300>
  <img src="Outputs/output5.jpeg" alt="Lowpoly" width=400 height=300>
</div>

# Installation

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
	        implementation 'com.github.abhriyaroy:LowpolyRxJava:1.0'
	}

That's it!!


# Example

###### Kotlin way - <br>

	private fun generateLowpoly(originalBitmap: Bitmap): Single<Bitmap> {
		return LowPoly.generate(originalBitmap)
		 // Observe on thread according to your need
      		.observeOn(AndroidSchedulers.mainThread())
	}
	
###### Java way - <br>
  
	private Single<Bitmap> generateLowpoly(Bitmap originalBitmap){
	   	 return LowPoly.generate(originalBitmap)
	    	 // Observe on thread according to your need
      		.observeOn(AndroidSchedulers.mainThread())
	}
	
<br>

###### A full implementation is in the app module of this repo.

### Please feel free to download the debug apk and try it out yourself!

Please note that using this library, it is assumed that RxJava and RxAndroid are already added as dependencies in your project.

If you do like my work, please hit the star button.
If you have any ideas to improve upon my work feel free to raise a PR and let's learn together. :)

# Inspiration 
https://github.com/zzhoujay/LowPolyAndroid 

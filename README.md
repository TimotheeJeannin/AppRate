AppRate
=======

* AppRate allows your users to rate your application and will optionally prompt for email feedback if they don't like your application.

* AppRate shows a customizable rate dialog according to your chosen settings.

* If enabled, then the user will first be asked if they like the application prior to asking them to rate it. If they respond that they don't like the application they will be prompted to leave email feedback. Both of these dialogs can be customized. 

Screenshots
-----------
![DoYouLikeApp Dark](AppRateScreenshots/screenshot_doyoulikeapp_dark.png "Do you like the app (dark)")
![DoYouLikeApp Light](AppRateScreenshots/screenshot_doyoulikeapp_light.png "Do you like the app (light)")

![Screenshot 1](AppRateScreenshots/screenshot_1.png "Screenshot 1")
![Screenshot 2](AppRateScreenshots/screenshot_2.png "Screenshot 2")

![Send Feedback Dark](AppRateScreenshots/screenshot_sendfeedback_dark.png "Send Feedback (dark)")
![Send Feedback Light](AppRateScreenshots/screenshot_sendfeedback_light.png "Send Feedback (light)")


How to install and use
----------------------

1. Put the AppRate [jar] in your `libs` folder or add AppRate as a library project.

[jar]: AppRateDownloads

2. Use AppRate as follows in your `MAIN` activity: 

```java
new AppRate(this).init();
```

Features
--------

* You can decide **not to prompt the user** if the application **has crashed once**.

```java
new AppRate(this)
	.setShowIfAppHasCrashed(false)
	.init();
```

* You can decide **to reprompt the user** if the application **has been upgraded**.

```java
new AppRate(this)
	.setResetOnAppUpgrade(true)
	.init();
```

* You can decide **when to prompt the user**.

```java
new AppRate(this)
	.setMinDaysUntilPrompt(7)
	.setMinLaunchesUntilPrompt(20)
	.init();
```

* You can decide to **ask the user** if they **like the application first**.

```java
new AppRate(this)
	.showDoYouLikeTheAppFlow("support@your_support_email_address.com")
	.init();
```

* You can **customize** the send feedback **email subject**.

```java
new AppRate(this)
	.showDoYouLikeTheAppFlow("support@your_support_email_address.com")
	.setSendFeedbackSubject("Subject")
	.init();
```

* You can **customize** the send feedback **email body**.

```java
new AppRate(this)
	.showDoYouLikeTheAppFlow("support@your_support_email_address.com")
	.setSendFeedbackBody("Body")
	.init();
```

* You can **customize** all the messages and buttons of **the rate dialog**.

```java
AlertDialog.Builder builder = new AlertDialog.Builder(this)
	.setCustomTitle(myCustomTitleView)
	.setIcon(R.drawable.my_custom_icon)
	.setMessage("My custom message")
	.setPositiveButton("My custom positive button", null)
	.setNegativeButton("My custom negative button", null)
	.setNeutralButton("My custom neutral button", null);

new AppRate(this)
	.setCustomDialog(builder)
	.init();
```

* You can set **your own click listener** on **the rate dialog**.

```java
new AppRate(this)
	.setOnClickListener(new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Log.v(TAG, "Rate dialog positive button clicked");
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					Log.v(TAG, "Rate dialog negative button clicked");
					break;
				case DialogInterface.BUTTON_NEUTRAL:
					Log.v(TAG, "Rate dialog neutral button clicked");
					break;
				default:
				break;
			}
		}
	})
	.init();
```

* You can **customize** all the messages and buttons of **the do you like the app dialog**.

```java
AlertDialog.Builder builder = new AlertDialog.Builder(this)
	.setTitle("Like Us?")
	.setMessage("Do you totally dig us?")
	.setPositiveButton("Heck Yes", null)
	.setNegativeButton("No!!!", null);

new AppRate(this)
	.setCustomDoYouLikeAppDialog(builder)
	.init();
```

* You can set **your own click listener** on **the do you like the app dialog**.

```java
new AppRate(this)
	.setDoYouLikeAppOnClickListener(new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Log.v(TAG, "Do you like the app dialog positive button clicked");
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					Log.v(TAG, "Do you like the app dialog negative button clicked");
					break;
				default:
				break;
			}
		}
	})
	.init();
```

* You can **customize** all the messages and buttons of **the send feedback dialog**.

```java
AlertDialog.Builder builder = new AlertDialog.Builder(this)
	.setTitle("Help us out")
	.setMessage("Want to tell us what you don't like?")
	.setPositiveButton("Okay", null)
	.setNegativeButton("No", null);

new AppRate(this)
	.setCustomSendFeedbackDialog(builder)
	.init();
```

* You can set **your own click listener** on **the do you like the app dialog**.

```java
new AppRate(this)
	.setSendFeedbackOnClickListener(new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Log.v(TAG, "Send feedback dialog positive button clicked");
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					Log.v(TAG, "Send feedback dialog negative button clicked");
					break;
				default:
				break;
			}
		}
	})
	.init();
```

License
-------

This content is released under the MIT License.

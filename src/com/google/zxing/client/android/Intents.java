/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

/**
 * This class provides the constants to use when sending an Intent to Barcode
 * Scanner. These strings are effectively API and cannot be changed.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {
	private Intents() {
	}

	public static final class Encode {
		/**
		 * Send this intent to encode a piece of data as a QR code and display
		 * it full screen, so that another person can scan the barcode from your
		 * screen.
		 */
		public static final String ACTION = "com.google.zxing.client.android.ENCODE";

		/**
		 * The data to encode. Use
		 * {@link android.content.Intent#putExtra(String, String)} or
		 * {@link android.content.Intent#putExtra(String, android.os.Bundle)},
		 * depending on the type and format specified. Non-QR Code formats
		 * should just use a String here. For QR Code, see Contents for details.
		 */
		public static final String DATA = "ENCODE_DATA";

		/**
		 * The type of data being supplied if the format is QR Code. Use
		 * {@link android.content.Intent#putExtra(String, String)} with one of
		 * {@link Contents.Type}.
		 */
		public static final String TYPE = "ENCODE_TYPE";

		/**
		 * The barcode format to be displayed. If this isn't specified or is
		 * blank, it defaults to QR Code. Use
		 * {@link android.content.Intent#putExtra(String, String)}, where format
		 * is one of {@link com.google.zxing.BarcodeFormat}.
		 */
		public static final String FORMAT = "ENCODE_FORMAT";

		/**
		 * Normally the contents of the barcode are displayed to the user in a
		 * TextView. Setting this boolean to false will hide that TextView,
		 * showing only the encode barcode.
		 */
		public static final String SHOW_CONTENTS = "ENCODE_SHOW_CONTENTS";

		private Encode() {
		}
	}

}

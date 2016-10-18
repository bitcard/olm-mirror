/*
 * Copyright 2016 OpenMarket Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matrix.olm;

import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

public class OlmUtility implements Serializable {
    private static final String LOG_TAG = "OlmUtility";

    /** raw pointer value returned by JNI.
     * this value uniquely identifies this utility instance.
     **/
    private long mNativeOlmUtilityId;

    public OlmUtility() {
        initUtility();
    }

    /**
     * Getter on the session ID.
     * @return native session ID
     */
    public long getOlmUtilityId(){
        return mNativeOlmUtilityId;
    }

    /**
     * Create a native utility instance.
     * To be called before any other API call.
     * @return true if init succeed, false otherwise.
     */
    private boolean initUtility() {
        boolean retCode = false;
        if(0 != (mNativeOlmUtilityId = initUtilityJni())){
            retCode = true;
        }
        return retCode;
    }
    private native long initUtilityJni();

    /**
     * Release native instance.<br>
     * Public API for {@link #releaseUtilityJni()}.
     */
    public void releaseUtility(){
        releaseUtilityJni();
        mNativeOlmUtilityId = 0;
    }
    private native void releaseUtilityJni();

    /**
     * Verify an ed25519 signature.<br>
     * If the signature is verified, the method returns true. If false is returned, an error description is provided in aError.
     * If the key was too small, aError is set to "OLM.INVALID_BASE64".
     * If the signature was invalid, aError is set to "OLM.BAD_MESSAGE_MAC".<br>
     * @param aSignature the base64-encoded message signature to be checked.
     * @param aFingerprintKey the ed25519 key
     * @param aMessage the message which was signed
     * @param aError error message description
     * @return true if the signature is verified, false otherwise
     */
    public boolean verifyEd25519Signature(String aSignature, String aFingerprintKey, String aMessage, String aError) {
        boolean retCode = false;
        OlmUtility retObj=null;

        if(null == aError) {
            Log.e(LOG_TAG, "## verifyEd25519Signature(): invalid input error parameter");
        }
        else if(TextUtils.isEmpty(aSignature) || TextUtils.isEmpty(aFingerprintKey) || TextUtils.isEmpty(aMessage)){
            Log.e(LOG_TAG, "## verifyEd25519Signature(): invalid input parameters");
        } else {
            String errorRetValue = verifyEd25519SignatureJni(aSignature,aFingerprintKey, aMessage);
            if(null == errorRetValue) {
                aError="";
                retCode = true;
            } else {
                aError = errorRetValue;
            }
        }

        return retCode;
    }
    private native String verifyEd25519SignatureJni(String aSignature, String aFingerprintKey, String aMessage);


    /**
     * Compute the hash(SHA-256) value of the string given in parameter(aMessageToHash).<br>
     * The hash value is the returned by the method.
     * @param aMessageToHash message to be hashed
     * @return hash value if operation succeed, null otherwise
     */
     public String sha256(String aMessageToHash) {
        String hashRetValue = null;

         if(null != aMessageToHash){
            hashRetValue = sha256Jni(aMessageToHash);
         }

        return hashRetValue;

    }
    private native String sha256Jni(String aMessage);


    // TODO missing API: initWithSerializedData
    // TODO missing API: serializeDataWithKey
    // TODO missing API: initWithCoder
    // TODO missing API: encodeWithCoder
}

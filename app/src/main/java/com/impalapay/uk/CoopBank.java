package com.impalapay.uk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.cardinalcommerce.cardinalmobilesdk.Cardinal;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType;
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters;
import com.cardinalcommerce.shared.userinterfaces.UiCustomization;

import org.json.JSONArray;

public class CoopBank extends Application {

    private Cardinal cardinal = Cardinal.getInstance();

    protected void onCreate(Bundle savedInstanceState) {

        CardinalConfigurationParameters cardinalConfigurationParameters = new CardinalConfigurationParameters();
        cardinalConfigurationParameters.setEnvironment(CardinalEnvironment.STAGING);
        cardinalConfigurationParameters.setRequestTimeout(8000);
        cardinalConfigurationParameters.setChallengeTimeout(5);
        JSONArray rType = new JSONArray();
        rType.put(CardinalRenderType.OTP);
        rType.put(CardinalRenderType.SINGLE_SELECT);
        rType.put(CardinalRenderType.MULTI_SELECT);
        rType.put(CardinalRenderType.OOB);
        rType.put(CardinalRenderType.HTML);
        cardinalConfigurationParameters.setRenderType(rType);

        cardinalConfigurationParameters.setUiType(CardinalUiType.BOTH);

        UiCustomization yourUICustomizationObject = new UiCustomization();
        cardinalConfigurationParameters.setUICustomization(yourUICustomizationObject);

        cardinal.configure(this,cardinalConfigurationParameters);
    }

    }

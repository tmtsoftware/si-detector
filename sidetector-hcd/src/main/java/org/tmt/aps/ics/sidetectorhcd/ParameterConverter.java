package org.tmt.aps.ics.sidetectorhcd;

import csw.params.core.generics.Key;
import csw.params.core.generics.Parameter;
import csw.params.core.models.Struct;
import csw.params.javadsl.JKeyType;
import org.tmt.aps.ics.sidetector.api.ParameterItemInfo;
import org.tmt.aps.ics.sidetector.api.PulldownItem;


import java.util.ArrayList;
import java.util.List;

public class ParameterConverter {

    public static Parameter<Struct> convertParameterItemListToParameterParam(String parameterListName, List<ParameterItemInfo> parameterItemInfoList) {


        Key<Struct> parameterItemKey = JKeyType.StructKey().make(parameterListName);

        Key<String> displayNameKey = JKeyType.StringKey().make("displayName");
        Key<String> valStrKey = JKeyType.StringKey().make("valStr");
        Key<String> unitStrKey = JKeyType.StringKey().make("unitStr");
        Key<Integer> unitTypeKey = JKeyType.IntKey().make("unitType");
        Key<String> stepStringKey = JKeyType.StringKey().make("stepStr");
        Key<Boolean> writeableKey = JKeyType.BooleanKey().make("writeable");
        Key<String> maxStrKey = JKeyType.StringKey().make("maxStrKey");
        Key<String> minStrKey = JKeyType.StringKey().make("minStrKey");


        Key<String> pullDownItemNamesKey = JKeyType.StringKey().make("pullDownItemNamesKey");
        Key<String> pullDownItemValuesKey = JKeyType.StringKey().make("pullDownItemValuesKey");


        List<Struct> structArrayList = new ArrayList<Struct>();

        for (ParameterItemInfo parameterItemInfo : parameterItemInfoList) {

            // use multiple valued keys to 'flatten' pull down object

            ArrayList<String> pulldownItemNames = new ArrayList<String>();
            ArrayList<String> pulldownItemValues = new ArrayList<String>();

            if (parameterItemInfo.getPulldownItemList() != null) {
                for (PulldownItem pulldownItem : parameterItemInfo.getPulldownItemList()) {

                    pulldownItemNames.add(pulldownItem.getPulldownItemName());
                    pulldownItemValues.add(pulldownItem.getPulldownItemValue());
                }

            }

            Struct parameterItemStruct = new Struct().madd(
                    displayNameKey.set(parameterItemInfo.getDisplayName()),
                    valStrKey.set(parameterItemInfo.getValStr()),
                    unitStrKey.set((parameterItemInfo.getUnitStr())),
                    unitTypeKey.set(parameterItemInfo.getUnitType()),
                    stepStringKey.set(parameterItemInfo.getStepStr()),
                    writeableKey.set(parameterItemInfo.isWriteable()),
                    maxStrKey.set(parameterItemInfo.getMaxStr()),
                    minStrKey.set(parameterItemInfo.getMinStr()),
                    pullDownItemNamesKey.set(pulldownItemNames.toArray(new String[0])),
                    pullDownItemValuesKey.set(pulldownItemValues.toArray(new String[0])));


            structArrayList.add(parameterItemStruct);
        }

        Parameter<Struct> parameter = parameterItemKey.set(structArrayList.toArray(new Struct[0]));

        return parameter;

    }



}

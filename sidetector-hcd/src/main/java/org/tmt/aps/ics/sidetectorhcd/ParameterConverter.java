package org.tmt.aps.ics.sidetectorhcd;

import csw.params.core.generics.Key;
import csw.params.core.generics.Parameter;
import csw.params.core.models.Struct;
import csw.params.javadsl.JKeyType;
import org.tmt.aps.ics.sidetector.api.BitField;
import org.tmt.aps.ics.sidetector.api.ParameterItemInfo;
import org.tmt.aps.ics.sidetector.api.PulldownItem;


import java.util.ArrayList;
import java.util.List;

public class ParameterConverter {



    static Key<String> displayNameKey = JKeyType.StringKey().make("displayName");
    static Key<String> valStrKey = JKeyType.StringKey().make("valStr");
    static Key<String> unitStrKey = JKeyType.StringKey().make("unitStr");
    static Key<Integer> unitTypeKey = JKeyType.IntKey().make("unitType");
    static Key<String> stepStringKey = JKeyType.StringKey().make("stepStr");
    static Key<Boolean> writeableKey = JKeyType.BooleanKey().make("writeable");
    static Key<String> maxStrKey = JKeyType.StringKey().make("maxStrKey");
    static Key<String> minStrKey = JKeyType.StringKey().make("minStrKey");


    static Key<String> pullDownItemNamesKey = JKeyType.StringKey().make("pullDownItemNamesKey");
    static Key<String> pullDownItemValuesKey = JKeyType.StringKey().make("pullDownItemValuesKey");

    static Key<String> bitFieldsDisplayNamesKey = JKeyType.StringKey().make("bitFieldDisplayNames");
    static Key<Integer> bitFieldBitPositionsKey = JKeyType.IntKey().make("bitFieldBitPositions");



    public static Parameter<Struct> convertParameterItemListToParameterParam(String parameterListName, List<ParameterItemInfo> parameterItemInfoList) {

        Key<Struct> parameterItemKey = JKeyType.StructKey().make(parameterListName);


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


    public static Parameter<Struct> convertParameterItemListToParameterParam(List<ParameterItemInfo> parameterItemInfoList) {

        Key<Struct> parameterItemKey = JKeyType.StructKey().make("commandsStruct");


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

            // use multiple valued keys to 'flatten' bit fields object

            ArrayList<String> bitFieldsDisplayNames = new ArrayList<String>();
            ArrayList<Integer> bitFieldsbitPositions = new ArrayList<Integer>();

            if (parameterItemInfo.getBitFieldList() != null) {
                for (BitField bitField : parameterItemInfo.getBitFieldList()) {

                    bitFieldsDisplayNames.add(bitField.getDisplayName());
                    bitFieldsbitPositions.add(bitField.getBitPos());
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
                    pullDownItemValuesKey.set(pulldownItemValues.toArray(new String[0])),
                    bitFieldsDisplayNamesKey.set(bitFieldsDisplayNames.toArray(new String[0])),
                    bitFieldBitPositionsKey.set(bitFieldsbitPositions.toArray(new Integer[0])));



            structArrayList.add(parameterItemStruct);
        }

        Parameter<Struct> parameter = parameterItemKey.set(structArrayList.toArray(new Struct[0]));

        return parameter;

    }



}

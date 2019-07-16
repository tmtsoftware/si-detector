package org.tmt.aps.ics.sidetectorhcd;

import csw.params.core.generics.Key;
import csw.params.core.generics.Parameter;
import csw.params.core.models.Struct;
import csw.params.javadsl.JKeyType;
import org.tmt.aps.ics.sidetector.api.BitField;
import org.tmt.aps.ics.sidetector.api.StatusItemInfo;


import java.util.ArrayList;
import java.util.List;


public class StatusConverter {

    public static Parameter<Struct> convertStatusItemListToStatusParam(List<StatusItemInfo> statusItemInfoList) {


        Key<Struct> statusItemKey = JKeyType.StructKey().make("statusItemStruct");

        Key<String> displayStrKey = JKeyType.StringKey().make("displayStr");
        Key<String> valStrKey = JKeyType.StringKey().make("valStr");
        Key<String> unitStrKey = JKeyType.StringKey().make("unitStr");
        Key<Integer> unitTypeKey = JKeyType.IntKey().make("unitType");
        Key<String> stepStringKey = JKeyType.StringKey().make("stepStr");
        Key<Boolean> writeableKey = JKeyType.BooleanKey().make("writeable");

        Key<String> bitFieldsDisplayNamesKey = JKeyType.StringKey().make("bitFieldDisplayNames");
        Key<Integer> bitFieldBitPositionsKey = JKeyType.IntKey().make("bitFieldBitPositions");


        List<Struct> structArrayList = new ArrayList<Struct>();

        for (StatusItemInfo statusItemInfo : statusItemInfoList) {

            // use multiple valued keys to 'flatten' bit fields object

            ArrayList<String> bitFieldsDisplayNames = new ArrayList<String>();
            ArrayList<Integer> bitFieldsbitPositions = new ArrayList<Integer>();

            if (statusItemInfo.getBitFieldList() != null) {
                for (BitField bitField : statusItemInfo.getBitFieldList()) {

                    bitFieldsDisplayNames.add(bitField.getDisplayName());
                    bitFieldsbitPositions.add(bitField.getBitPos());
                }

            }

            Struct statusItemStruct = new Struct().madd(
                    displayStrKey.set(statusItemInfo.getDisplayStr()),
                    valStrKey.set(statusItemInfo.getValStr()),
                    unitStrKey.set((statusItemInfo.getUnitStr())),
                    unitTypeKey.set(statusItemInfo.getUnitType()),
                    stepStringKey.set(statusItemInfo.getStepStr()),
                    writeableKey.set(statusItemInfo.isWriteable()),
                    bitFieldsDisplayNamesKey.set(bitFieldsDisplayNames.toArray(new String[0])),
                    bitFieldBitPositionsKey.set(bitFieldsbitPositions.toArray(new Integer[0])));


            structArrayList.add(statusItemStruct);
        }

        Parameter<Struct> parameter = statusItemKey.set(structArrayList.toArray(new Struct[0]));

        return parameter;

    }



}

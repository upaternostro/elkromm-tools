package org.paternostro.elkromm.tools.model;

import org.paternostro.elkromm.dto.AreasAndPartitions;
import org.paternostro.elkromm.dto.C200bParameters;
import org.paternostro.elkromm.dto.Credential;
import org.paternostro.elkromm.dto.Expansion;
import org.paternostro.elkromm.dto.Keyboard;
import org.paternostro.elkromm.dto.PSTNGSM;
import org.paternostro.elkromm.dto.ParametersEnablings;
import org.paternostro.elkromm.dto.PeripheralUnits;
import org.paternostro.elkromm.dto.PhoneNumbersSendingCodes;
import org.paternostro.elkromm.dto.PhoneParameters;
import org.paternostro.elkromm.dto.Reader;
import org.paternostro.elkromm.dto.SMSs;
import org.paternostro.elkromm.dto.TimeProgrammer;
import org.paternostro.elkromm.tools.serializers.AreasAndPartitionsSerializer;
import org.paternostro.elkromm.tools.serializers.C200bParametersSerializer;
import org.paternostro.elkromm.tools.serializers.ExpansionsSerializer;
import org.paternostro.elkromm.tools.serializers.PeripheralUnitsSerializer;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonRootName(value = "MP-508")
public class Model {
    public int                      plantCode;
    @JsonSerialize(using = PeripheralUnitsSerializer.class)
    public PeripheralUnits          peripheralUnits = null;
    @JsonSerialize(using = ExpansionsSerializer.class)
    public Expansion[]              expansions = null;
    public Keyboard[]               keypads = null;
    public Reader[]                 readers = null;
    public TimeProgrammer           timeProgrammer = null;
    @JsonSerialize(using = AreasAndPartitionsSerializer.class)
    public AreasAndPartitions       areasAndPartitions = null;
    public PhoneParameters          phoneParameters = null;
    public PhoneNumbersSendingCodes phoneNumbersSendingCodes = null;
    @JsonSerialize(using = C200bParametersSerializer.class)
    public C200bParameters          c200bParameters = null;
    public SMSs                     sMSs = null;
    public PSTNGSM                  pstnGsm = null;
    public Credential[]             users = null;
    public Credential[]             keys = null;
    public ParametersEnablings      parametersEnablings = null;
}

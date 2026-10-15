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

/**
 * Everything an MP-508 panel is configured with, as read from or written to a
 * {@link org.paternostro.elkromm.tools.Medium}.
 * <p>
 * A plain data holder: one public field per part of the configuration, each of them
 * corresponding to a command of the Elkron protocol and typed with the matching
 * {@link org.paternostro.elkromm.dto} class of the elkromm library. A field is {@code null} when
 * that part has not been read, or does not exist (for instance keypads on a panel without
 * keypads).
 * <p>
 * The JSON representation is produced by Jackson, using the serializers in
 * {@link org.paternostro.elkromm.tools.serializers} for some of the parts.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
@JsonRootName(value = "MP-508")
public class Model {
    /**
     * Plant identification code of the panel.
     */
    public int                      plantCode;
    /**
     * Addresses of the peripheral units (keypads, readers, expansions) connected to the panel.
     */
    @JsonSerialize(using = PeripheralUnitsSerializer.class)
    public PeripheralUnits          peripheralUnits = null;
    /**
     * Expansions, with their inputs and outputs.
     */
    @JsonSerialize(using = ExpansionsSerializer.class)
    public Expansion[]              expansions = null;
    /**
     * Keypads programming; {@code null} if there are none.
     */
    public Keyboard[]               keypads = null;
    /**
     * Proximity readers programming; {@code null} if there are none.
     */
    public Reader[]                 readers = null;
    /**
     * Time programmer.
     */
    public TimeProgrammer           timeProgrammer = null;
    /**
     * Areas and partitions.
     */
    @JsonSerialize(using = AreasAndPartitionsSerializer.class)
    public AreasAndPartitions       areasAndPartitions = null;
    /**
     * Phone parameters.
     */
    public PhoneParameters          phoneParameters = null;
    /**
     * Phone numbers and sending codes.
     */
    public PhoneNumbersSendingCodes phoneNumbersSendingCodes = null;
    /**
     * Parameters of the C200B remote receiver.
     */
    @JsonSerialize(using = C200bParametersSerializer.class)
    public C200bParameters          c200bParameters = null;
    /**
     * SMS messages.
     */
    public SMSs                     sMSs = null;
    /**
     * PSTN and GSM parameters.
     */
    public PSTNGSM                  pstnGsm = null;
    /**
     * Users.
     */
    public Credential[]             users = null;
    /**
     * Keys.
     */
    public Credential[]             keys = null;
    /**
     * Enablings of the panel parameters.
     */
    public ParametersEnablings      parametersEnablings = null;
}

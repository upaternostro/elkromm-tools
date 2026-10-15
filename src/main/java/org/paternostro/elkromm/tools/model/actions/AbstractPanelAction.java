package org.paternostro.elkromm.tools.model.actions;

import org.paternostro.elkromm.ElkrommFacade;

/**
 * Base class of the readers and writers that work on a live panel through an
 * {@link ElkrommFacade}, which the caller has connected and logged in, and remains responsible
 * for closing.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public abstract class AbstractPanelAction
{
    /**
     * The facade used to talk to the panel.
     */
    protected ElkrommFacade   facade;

    /**
     * Creates the action.
     *
     * @param param the facade to use, which must be logged in
     * @throws IllegalStateException if the facade is not logged in
     */
    public AbstractPanelAction(ElkrommFacade param)
    {
        if (param.getStatus() != ElkrommFacade.Status.ST_LOGGED_IN) throw new IllegalStateException("Facade is not logged in (status: " + param.getStatus() + ")");

        this.facade = param;
    }
}

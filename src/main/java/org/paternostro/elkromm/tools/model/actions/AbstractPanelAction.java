package org.paternostro.elkromm.tools.model.actions;

import org.paternostro.elkromm.ElkrommFacade;

public abstract class AbstractPanelAction
{
    protected ElkrommFacade   facade;

    public AbstractPanelAction(ElkrommFacade param)
    {
        if (param.getStatus() != ElkrommFacade.Status.ST_LOGGED_IN) throw new IllegalStateException("Facade is not logged in (status: " + param.getStatus() + ")");

        this.facade = param;
    }
}

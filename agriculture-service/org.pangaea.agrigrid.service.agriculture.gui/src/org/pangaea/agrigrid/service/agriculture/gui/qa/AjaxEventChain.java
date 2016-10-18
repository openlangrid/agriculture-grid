package org.pangaea.agrigrid.service.agriculture.gui.qa;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;

public interface AjaxEventChain {
	public void delete(AjaxRequestTarget target, MarkupContainer eventBody);
}

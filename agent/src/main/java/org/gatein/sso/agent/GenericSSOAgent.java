/*
 * JBoss, a division of Red Hat
 * Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.sso.agent;

import java.io.IOException;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.login.InitiateLoginServlet;

import org.gatein.sso.agent.cas.CASAgent;
import org.gatein.sso.agent.opensso.OpenSSOAgent;

/**
 * @author <a href="mailto:sshah@redhat.com">Sohil Shah</a>
 */
public class GenericSSOAgent extends InitiateLoginServlet
{
	private static Logger log = Logger.getLogger(GenericSSOAgent.class);
	
	private String casServerUrl;
	
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		this.casServerUrl = this.getServletConfig().getInitParameter("casServerUrl");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		try
		{
			this.processSSOToken(req);		
			super.doGet(req, resp);
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		this.doGet(req, resp);
	}

	private void processSSOToken(HttpServletRequest httpRequest) throws Exception
	{
		String ticket = httpRequest.getParameter("ticket");
		String jossoAssertion = httpRequest.getParameter("josso_assertion_id");

		if (ticket != null && ticket.trim().length() > 0)
		{
			CASAgent.getInstance(this.casServerUrl).validateTicket(httpRequest, ticket);
		}
		else if (jossoAssertion != null && jossoAssertion.trim().length() > 0)
		{
			//TODO: fix the JOSSO Agent. This will need to the new client side JOSSO stack that can run on 5.1.0.GA
		}
		else
		{
			//See if an OpenSSO Token was used
			OpenSSOAgent.getInstance().validateTicket(httpRequest);
		}
	}		
}
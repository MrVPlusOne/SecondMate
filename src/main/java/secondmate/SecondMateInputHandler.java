/**
 * SecondMateInputHandler.java - FirstMate plugin
 *
 * Copyright 2006-2008 Ollie Rutherfurd <oliver@rutherfurd.net>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id$
 */
package secondmate;

//{{{ imports

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultInputHandler;
import org.gjt.sp.jedit.gui.InputHandler;
import org.gjt.sp.jedit.gui.KeyEventTranslator;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

import java.awt.event.KeyEvent;
//}}}

interface IHandler{
	boolean handleKey(KeyEventTranslator.Key keyStroke);
}

/**
 * Input handle to do Auto-Character Pairing, etc...
 */
public class SecondMateInputHandler extends DefaultInputHandler
{
	//{{{ constants
	private static final String APOSTROPHE = "apostrophe";
	private static final String QUOTE = "quote";
	private static final String PARENTHESIS = "parenthesis";
	private static final String BRACKET = "bracket";
	private static final String BRACE = "brace";
	//}}}

	//{{{ SecondMateInputHandler(View, DefaultInputHandler)
	public SecondMateInputHandler(View view, DefaultInputHandler defaultInputHandler)
	{
		super(view,defaultInputHandler);
		this.view = view;
		this.defaultInputHandler = defaultInputHandler;
		this.scalaHandler = new SecondMateInputHandlerScala(view);
	} //}}}


	public boolean handleKey(KeyEventTranslator.Key keyStroke, boolean dryRun){
		if(dryRun)
			return defaultInputHandler.handleKey(keyStroke, dryRun);

		return scalaHandler.handleKey(keyStroke,
					ks -> super.handleKey(ks, false)
				);
//		return defaultInputHandler.handleKey(keyStroke, dryRun);
	}


	//{{{ getDefaultHandler() method
	public InputHandler getDefaultHandler()
	{
		return this.defaultInputHandler;
	} //}}}

	//{{{ getPairEnabled() method
	private boolean getPairEnabled(String mode, String type)
	{
		return jEdit.getBooleanProperty("mode."+mode+".pair."+type,true);
	}//}}}

	//{{{ privates
	private View view;
	private InputHandler defaultInputHandler;
	private boolean canUndo = false;
	private SecondMateInputHandlerScala scalaHandler;
	//}}}
}

// :folding=explicit:collapseFolds=1:tabSize=4:noTabs=false:

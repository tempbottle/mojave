/*
 * Copyright (C) 2011 Mojavemvc.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mojavemvc.exception;

import org.mojavemvc.FrontController;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

/**
 * The default {@link ErrorHandler} for the application. Simply
 * returns a {@link JspView} based on the 'jsp-error-file' init
 * parameter (see {@link org.mojavemvc.FrontController}).
 * 
 * @author Luis Antunes
 */
public class DefaultJspErrorHandler implements ErrorHandler {

	public View handleError( Throwable e ) {
		
		return new JspView( FrontController.getJspErrorFile( ) );
	}
}
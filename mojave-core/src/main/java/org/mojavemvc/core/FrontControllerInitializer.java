/*
public class FrontControllerInitializer {

	private static final Logger logger = LoggerFactory.getLogger(FrontControllerInitializer.class);
	
	private static final String CONTROLLER_CLASS_NAMESPACE = "controller-classes";
	private static final String JSP_PATH = "jsp-path";
	private static final String JSP_ERROR_FILE = "jsp-error-file";
	private static final String CONTROLLER_VARIABLE = "controller-variable";
	private static final String ACTION_VARIABLE = "action-variable";
	private static final String GUICE_MODULES = "guice-modules";
	
	private String controllerClassNamespace;
	private String controllerVariable;
	private String actionVariable;
	private String jspPath;
	private String jspErrorFile;
	private String guiceModulesNamespace;
	
	private final ServletConfig servletConfig;
	private final ControllerContext context;
	
	public FrontControllerInitializer( ServletConfig servletConfig, ControllerContext context ) {
		this.servletConfig = servletConfig;
		this.context = context;
	}
	
	public void performInitialization( ) {
		
		readInitParams( );
		createGuiceInjector( );
		createControllerDatabase( );
	}
	
	private void readInitParams( ) {
		
		logger.debug("reading init parameters...");
		readControllerClassNamespace( );
		readJspPath( );
		readJspErrorFile( );
		readControllerVariable( );
		readActionVariable( );
		readGuiceModulesNamespace( );
	}
	
	private void readControllerClassNamespace( ) {
		
		controllerClassNamespace = servletConfig.getInitParameter( CONTROLLER_CLASS_NAMESPACE );
		if ( isEmpty( controllerClassNamespace ) ) {
			
			throw new ConfigurationException("controller class namespace must be specified in " +
					"web.xml as servlet " + CONTROLLER_CLASS_NAMESPACE + " init-param.");
		}
	}
	
	private void readJspPath( ) {
		
		jspPath = servletConfig.getInitParameter( JSP_PATH );
		if (!isEmpty( jspPath) ) {
			
			jspPath = processContextPath(jspPath);
			logger.debug( "setting " + JSP_PATH + " to " + jspPath );
			
		} else {
			
			jspPath = "";
			logger.debug( "no " + JSP_PATH + 
					" init-param specified; setting to \"\"" );
		}
	}
	
	private void readJspErrorFile( ) {
		
		jspErrorFile = servletConfig.getInitParameter( JSP_ERROR_FILE );
		if (!isEmpty( jspErrorFile) ) {
			
			logger.debug( "setting " + JSP_ERROR_FILE + " to " + jspErrorFile );
			
		} else {
			
			jspErrorFile = "error.jsp";
			logger.debug( "no " + JSP_ERROR_FILE + 
					" init-param specified; setting to " + jspErrorFile );
		}
	}
	
	private void readControllerVariable( ) {
		
		controllerVariable = servletConfig.getInitParameter( CONTROLLER_VARIABLE );
		if (!isEmpty( controllerVariable )) {
			
			logger.debug( "setting " + CONTROLLER_VARIABLE + " to " + controllerVariable );
			
		} else {
			
			controllerVariable = "cntrl";
			logger.debug( "no " + CONTROLLER_VARIABLE + 
					" init-param specified; setting to " + controllerVariable );
		}
	}
	
	private void readActionVariable( ) {
		
		actionVariable = servletConfig.getInitParameter( ACTION_VARIABLE );
		if (!isEmpty( actionVariable )) {
			
			logger.debug( "setting " + ACTION_VARIABLE + " to " + actionVariable );
			
		} else {
			
			actionVariable = "actn";
			logger.debug( "no " + ACTION_VARIABLE + 
					" init-param specified; setting to " + actionVariable );
		}
	}
	
	private void readGuiceModulesNamespace( ) {
		
		guiceModulesNamespace = servletConfig.getInitParameter( GUICE_MODULES );
	}
	
	private void createGuiceInjector( ) {

		logger.debug( "creating Guice Injector..." );
		
		try {
			
			Set<Class<? extends AbstractModule>> moduleClasses = scanModuleClasses( );
			GuiceInitializer guiceInitializer = new GuiceInitializer( moduleClasses );
			Injector injector = guiceInitializer.initializeInjector( );
			context.setAttribute( GuiceInitializer.KEY, injector );
			
		} catch (Throwable e) {
			logger.error( "error initializing Guice", e );
		}
	}
	
	private void createControllerDatabase( ) {
		
		logger.debug("creating ControllerDatabase...");
		
		try {
			
			Set<Class<?>> controllerClasses = scanControllerClasses( );
			ControllerDatabase controllerDatabase = new MappedControllerDatabase( controllerClasses );
			context.setAttribute( ControllerDatabase.KEY, controllerDatabase );
			
		} 
		catch (Throwable e) {
			logger.error("error creating ControllerDatabase", e);
		}
	}
	
	private Set<Class<?>> scanControllerClasses( ) {
		
	}
	
	private Set<Class<? extends AbstractModule>> scanModuleClasses( ) {
		
	}

	private String processContextPath(String path) {
		
		if (path == null || path.trim().length() == 0) {
			return path;
		}
		if (path.charAt(path.length() - 1) != '/') {
			path += "/";
		}
		return path;
	}
	
	private boolean isEmpty( String arg ) {
		return arg == null || arg.trim().length() == 0;
	}

	public String getControllerVariable() {
		return controllerVariable;
	}

	public String getActionVariable() {
		return actionVariable;
	}

	public String getJspPath( ) {
		return jspPath;
	}

	public String getJspErrorFile( ) {
		return jspErrorFile;
	}
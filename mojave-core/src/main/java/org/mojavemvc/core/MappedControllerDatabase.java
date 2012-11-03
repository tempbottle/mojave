/*
 * A single instance of this class is meant to exist in the
 * front controller context, to be shared by multiple threads. The only methods
 * that can be called during requests are read methods. This class is
 * immutable once created, and thus is thread-safe.
 */
public class MappedControllerDatabase implements ControllerDatabase {

	private static final Logger logger = LoggerFactory.getLogger( MappedControllerDatabase.class );
	
	/*
	 * a map of the controller variable names to their classes 
	 * (eg. "index" -> org.mojavemvc.tests.IndexController)
	 */
	private final Map<String, Class<?>> controllerClassesMap = 
	
	/*
	 * a map of the controller classes to a map of their action names to method signatures
	 * eg. org.mojavemvc.tests.IndexController -> ["some-action" -> ActionSignature[ "someAction", [] ]]
	 */
	private final Map<Class<?>, Map<String, ActionSignature>> controllerClassToActionMap = 
	
	/*
	 * a map of the controller classes to their @BeforeAction methods
	 * eg. org.mojavemvc.tests.IndexController -> ActionSignature["doSomethingBefore"]
	 */
	private final Map<Class<?>, ActionSignature> controllerClassToBeforeActionMap = 
	
	/*
	 * a map of the controller classes to their @AfterAction methods
	 * eg. org.mojavemvc.tests.IndexController -> ActionSignature["doSomethingAfter"]
	 */
	private final Map<Class<?>, ActionSignature> controllerClassToAfterActionMap = 
	
	/*
	 * a map of the controller classes to their @DefaultAction methods
	 * eg. org.mojavemvc.tests.IndexController -> ActionSignature[ "someAction", [] ]]
	 */
	private final Map<Class<?>, ActionSignature> controllerClassToDefaultActionMap = 
	
	/*
	/*
	 * the application-wide default controller class, if any, as determined by the
	 * @DefaultController annotation
	 */
	private Class<?> defaultControllerClass = null;
	
	/**
	 * Construct a controller database based on the 
	 * given Set of controller Classes.
	 * 
	 * @param controllerClasses
	 */
	public MappedControllerDatabase( Set<Class<?>> controllerClasses ) {
		
		init( controllerClasses );
	}
	
	/**
	 * Get the conroller Class associated with the given
	 * controller variable name. 
	 * 
	 * @param controllerVariable
	 * @return the controller class associated with the variable name
	 */
	public Class<?> getControllerClass( String controllerVariable ) {
		
		return controllerClassesMap.get( controllerVariable );
	}
	
	/**
	 * Get the ActionSignature associated with the given
	 * controller class. ActionSignature is a thread-safe class.
	 * 
	 * @param controllerClass the controller class
	 * @return the ActionSignature associated with the controller class
	 */
	public ActionSignature getActionMethodSignature( Class<?> controllerClass, String action ) {
		
		Map<String, ActionSignature> actionMap = 
		if ( actionMap == null ) {
			throw new ConfigurationException( 
		}
		return actionMap.get( action );
	}
	
	/**
	 * Get the ActionSignature annotated with @BeforeAction for the
	 * given controller class. ActionSignature is thread-safe.
	 * 
	 * @param controllerClass the controller class
	 * @return the ActionSignature, or null if there is no 
	 */
	public ActionSignature getBeforeActionMethodFor( Class<?> controllerClass ) {
		
		return controllerClassToBeforeActionMap.get( controllerClass );
	}
	
	/**
	 * Get the ActionSignature annotated with @AfterAction for the
	 * given controller class. ActionSignature is thread-safe.
	 * 
	 * @param controllerClass the controller class
	 * @return the ActionSignature, or null if there is no 
	 */
	public ActionSignature getAfterActionMethodFor( Class<?> controllerClass ) {
		return controllerClassToAfterActionMap.get( controllerClass );
	}
	
	/**
	 * Get the ActionSignature annotated with @DefaultAction for the
	 * given controller class. ActionSignature is thread-safe.
	 * 
	 * @param controllerClass the controller class
	 * @return the ActionSignature, or null if there is no method 
	 */
	public ActionSignature getDefaultActionMethodFor( Class<?> controllerClass ) {
		return controllerClassToDefaultActionMap.get( controllerClass );
	}
	
	/**
	/**
	 * Get the default controller for the application, if specified.
	 * 
	 * @return the default controller class, or null if no @DefaultController is specified
	 */
	public Class<?> getDefaultControllerClass( ) {
		return defaultControllerClass;
	}
	
	/*--------------------------private methods----------------------------------------*/
	
	private void init( Set<Class<?>> controllerClasses ) {
		
		for ( Class<?> controllerClass : controllerClasses ) {
			
			logger.debug("found controller class: " + controllerClass.getName());

			Annotation controllerAnnotation = getControllerAnnotation( controllerClass );
				
			String controllerVariable = getControllerVariable( controllerClass, controllerAnnotation );
			addControllerClass( controllerVariable, controllerClass );
		}
	}
	
	private void addControllerClass( String controllerVariable, Class<?> controllerClass ) {
		
		/* check if this controller variable already exists;
		 * if so raise an exception */
		Class<?> existing = controllerClassesMap.get( controllerClass );
		if ( existing != null ) {
			throw new ConfigurationException( "a controller variable with the value " 
					+ controllerVariable + " already exists" );
		}
		controllerClassesMap.put( controllerVariable, controllerClass );
		checkForInitController( controllerClass );
		setActionMethodIndicesFor( controllerClass );
		setInterceptorsFor( controllerClass );
	}
	
	private void checkForDefaultController( Class<?> controllerClass ) {
		
		Annotation annot = controllerClass.getAnnotation( DefaultController.class );
		if (annot != null) {
			if (defaultControllerClass != null) {
				throw new ConfigurationException( "only one controller class can be " +
						"annotated with @" + DefaultController.class.getSimpleName( ) );
			}
			defaultControllerClass = controllerClass;
		}
	}
	
	private void setActionMethodIndicesFor( Class<?> controllerClass ) {
		
		Map<String, ActionSignature> actionMap = new HashMap<String, ActionSignature>( );
		
		Method[] methods = controllerClass.getDeclaredMethods( );
		for ( int i = 0; i < methods.length; i++ ) {
			
			Annotation ann = methods[i].getAnnotation(Action.class);
			if (ann != null) {
				Action actionAnn = (Action)ann;
				addActionSignature(controllerClass, action, methods[i], actionMap, fastClass);
				continue;
			}
			
			ann = methods[i].getAnnotation( BeforeAction.class );
			if (ann != null) {
				addBeforeOrAfterActionSignature(controllerClassToBeforeActionMap, 
				continue;
			}
			
			ann = methods[i].getAnnotation( AfterAction.class );
			if (ann != null) {
				addBeforeOrAfterActionSignature(controllerClassToAfterActionMap, 
				continue;
			}
			
			ann = methods[i].getAnnotation( DefaultAction.class );
			if (ann != null) {
				addDefaultActionSignature(controllerClassToDefaultActionMap, 
			}
		}
		controllerClassToActionMap.put( controllerClass, actionMap );
	}
	
	private void addActionSignature( Class<?> clazz, String action, Method method, 
			Map<String, ActionSignature> actionMap, FastClass fastClass ) {
		
		int fastIndex = fastClass.getIndex( method.getName( ), method.getParameterTypes( ) );
		
		ActionSignature sig = new BaseActionSignature( fastIndex, method.getName( ), method.getParameterTypes( ), 
				method.getParameterAnnotations( ) );
		actionMap.put( action, sig );
	}
} 
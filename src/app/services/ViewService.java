package app.services;

import javafx.scene.Scene;

/*
    De view service definieert een view welke toegang heeft tot de controller en de scene
    Deze scene en controller hebben een getter en setter
    De controller heeft een setter die zal alleen voor de eerste aanroep werken
 */
public abstract class ViewService {
	
	protected Scene scene;
	protected ControllerService controller;
	
	/*
		Zal de controller teruggeven
	 */
	public ControllerService getController() {
		return controller;
	}
	
	/*
		Zal de controller een waarde geven
	 */
	public void setController(ControllerService controller) {
		if (this.controller == null)
			this.controller = controller;
		else
			System.out.println("Cannot set controller, controller already set");
	}
	
	/*
		Zal de scene teruggeven
	 */
	public Scene getScene() {
		return scene;
	}
	
	/*
		Zal de scene een waarde geven
	 */
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
}

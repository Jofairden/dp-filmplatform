package app.services;

import app.models.MoviesListModel;

import java.util.Observer;

/*
    De controller service definieert een controller welke toegang heeft tot het model en de view
    Deze view heeft een getter en een setter
    De setter voor het model zal alleen voor de eerste aanroep werken
    Een ControllerService implements Observer, zodat deze als observer kan worden ingesteld op het Observable model
 */
public abstract class ControllerService implements Observer {
	
	protected MoviesListModel model;
	protected ViewService view;
	
	/*
		Zal het model property een waarde toekennen
	 */
	public void setModel(MoviesListModel model) {
		if (this.model == null)
			this.model = model;
		else
			System.out.println("Cannot set model, model already set");
	}
	
	/*
		Zal de view teruggeven
	 */
	public ViewService getView() {
		return view;
	}
	
	/*
		Zal de view property een waarde toekennen
	 */
	public void setView(ViewService view) {
		this.view = view;
	}
}

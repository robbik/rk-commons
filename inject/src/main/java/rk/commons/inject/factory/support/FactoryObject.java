package rk.commons.inject.factory.support;

public abstract class FactoryObject<T> {

	private T singleton;

	public FactoryObject() {
		singleton = null;
	}

	public T getObject() {
		if (singleton == null) {
			singleton = createInstance();
		}

		return singleton;
	}

	protected abstract T createInstance();
}

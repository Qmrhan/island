package com.oasisfeng.common.app;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;

import com.oasisfeng.android.databinding.ObservableSortedList;
import com.oasisfeng.island.BR;
import com.oasisfeng.island.model.AppViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View-model of basic entry-selectable (single-choice) app-list.
 *
 * Created by Oasis on 2016/6/24.
 */
public class BaseAppListViewModel<T extends AppViewModel> extends BaseObservable {

	private final ObservableSortedList<T> mApps;
	private final Map<String, T> mAppsByPackage = new HashMap<>();
	private transient T mSelection;

	// TODO: Rebuild the whole AbstractAppListViewModel to keep immutability?
	public void replaceApps(final List<T> apps) {
		mApps.clear();
		mAppsByPackage.clear();
		mApps.addAll(apps);
		for (final T app : apps)
			mAppsByPackage.put(app.info.packageName, app);
	}

	public T putApp(final String pkg, final T app) {
		final T old_app_vm = mAppsByPackage.put(pkg, app);
		if (old_app_vm != null) {
			final int index = mApps.indexOf(old_app_vm);
			mApps.updateItemAt(index, app);

			if (mSelection == old_app_vm) {
				app.selected.set(true);
				notifyPropertyChanged(BR.selection);
			}
		} else mApps.add(app);
		return app;
	}

	protected AppViewModel getAppAt(final int index) {
		return mApps.get(index);
	}

	protected int indexOf(final T app) {
		return mApps.indexOf(app);
	}

	/** STOP: This method only serves the generated binding class, and should never be called directly. */
	@Deprecated public ObservableList<T> getItems() {
		return mApps;
	}

	public void removeApp(final String pkg) {
		if (pkg == null) return;
		final T app = mAppsByPackage.remove(pkg);
		if (app == null) return;
		mApps.remove(app);
	}

	protected int size() { return mApps.size(); }

	/* Selection related */

	@Bindable public @Nullable T getSelection() { return mSelection; }

	public void clearSelection() {
		setSelection(null);
	}

	protected void setSelection(final T selection) {
		if (this.mSelection == selection) return;
		if (this.mSelection != null) this.mSelection.selected.set(false);
		this.mSelection = selection;
		if (selection != null) selection.selected.set(true);
		notifyPropertyChanged(BR.selection);
	}

	protected BaseAppListViewModel(final Class<T> clazz) {
		mApps = new ObservableSortedList<>(clazz);
	}
}

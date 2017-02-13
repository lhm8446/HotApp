package com.hotdog.hotapp.extraFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.hotdog.hotapp.R;

public class BaseFragment extends Fragment {
    protected void startFragment(FragmentManager fm, Class<? extends BaseFragment> fragmentClass) {
        BaseFragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (fragment == null) {
            throw new IllegalStateException("cannot start fragment. " + fragmentClass.getName());
        }
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack(null).commit();
    }

    protected void finishFragment() {
        getFragmentManager().popBackStack();
    }

    public void onPressedBackkey() {
        finishFragment();
    }
}


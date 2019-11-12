package com.example.myalbum.EditingPhoto;

import android.os.Bundle;

public interface CallBacks {
}

interface MainCallbacks {
    public void onMsgFromFragToMain (String sender, Bundle bundle);
}

interface FragmentCallbacks {
    public void onMsgFromMainToFragment(Bundle bundle);
}

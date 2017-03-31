package ai.resola.chatapp.support.customtabs;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by dotuan on 2017/03/30.
 */

public abstract class CustomTabsServiceConnection implements ServiceConnection {

    public CustomTabsServiceConnection() {
    }

    public final void onServiceConnected(final ComponentName name, IBinder service) {
        this.onCustomTabsServiceConnected(name, new CustomTabsClient(ICustomTabsService.Stub.asInterface(service), name) {
        });
    }

    public abstract void onCustomTabsServiceConnected(ComponentName var1, CustomTabsClient var2);
}

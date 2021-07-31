package sg.edu.np.mad.livre;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class CustomDrawerButton extends AppCompatButton implements DrawerLayout.DrawerListener {

    // This class is to create a Custom Button that works as a Hamburger Icon in Library Activity
    private DrawerLayout mDrawerLayout;

    public CustomDrawerButton(Context context) {
        super(context);
    }

    public CustomDrawerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Hide and unhide Button Visibility
    public void changeState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            setVisibility(GONE);
            mDrawerLayout.setVisibility(VISIBLE);
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    // Hide Drawer on close
    @Override
    public void onDrawerClosed(View drawerView) {
        setVisibility(VISIBLE);
        mDrawerLayout.setVisibility(GONE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public CustomDrawerButton setDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
        return this;
    }
}
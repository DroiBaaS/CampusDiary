package com.campus.diary.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

    private int textColor;

    protected SpannableClickable() {
        this.textColor = 0x8290AF;
    }

    protected SpannableClickable(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(textColor);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}

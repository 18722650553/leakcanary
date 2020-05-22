package com.example.leakcanary

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment

class TestFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val frame = FrameLayout(inflater.context)
    frame.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    val button = AppCompatButton(inflater.context)
    button.layoutParams =
      FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
          .also {
            it.gravity = Gravity.CENTER
          }
    frame.addView(button)

    button.text = "Click here!"
    button.setOnClickListener {
      TestDialogFragment().show(childFragmentManager, "test")
    }
    return frame
  }
}

class TestDialogFragment : AppCompatDialogFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return Button(inflater.context)
  }
}
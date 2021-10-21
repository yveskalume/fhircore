/*
 * Copyright 2021 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartregister.fhircore.engine.ui.base

import android.app.Activity
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.compose.material.AlertDialog
import org.smartregister.fhircore.engine.R
import org.smartregister.fhircore.engine.util.extension.hide
import org.smartregister.fhircore.engine.util.extension.show

enum class AlertIntent {
  PROGRESS,
  CONFIRM
}

object AlertDialogue {
  fun showAlert(
    context: Activity,
    alertIntent: AlertIntent,
    @StringRes message: Int,
    @StringRes title: Int? = null,
    confirmButtonListener: ((d: DialogInterface) -> Unit)? = null,
    @StringRes confirmButtonText: Int = R.string.questionnaire_alert_confirm_button_title,
    neutralButtonListener: ((d: DialogInterface) -> Unit)? = null,
    @StringRes neutralButtonText: Int = R.string.questionnaire_alert_neutral_button_title,
  ): AlertDialog {
    val dialog =
      AlertDialog.Builder(context)
        .apply {
          val view = context.layoutInflater.inflate(R.layout.alert_dialog, null)
          setView(view)
          title?.let { setTitle(it) }
          setCancelable(false)

          neutralButtonListener?.apply {
            setNeutralButton(neutralButtonText) { d, _ -> neutralButtonListener.invoke(d) }
          }
          confirmButtonListener?.apply {
            setPositiveButton(confirmButtonText) { d, _ -> confirmButtonListener.invoke(d) }
          }
        }
        .create()
        .apply { show() }

    dialog.findViewById<View>(R.id.pr_circular)?.apply {
      if (alertIntent == AlertIntent.PROGRESS) {
        this.show()
      } else this.hide()
    }

    dialog.findViewById<TextView>(R.id.tv_alert_message)?.apply { this.setText(message) }

    return dialog
  }

  fun showProgressAlert(context: Activity, @StringRes message: Int): AlertDialog {
    return showAlert(context, AlertIntent.PROGRESS, message)
  }

  fun hideProgressAlert(alert: AlertDialog) {
    if (alert.isShowing) alert.dismiss()
  }

  fun showConfirmAlert(
    context: Activity,
    @StringRes message: Int,
    @StringRes title: Int? = null,
    confirmButtonListener: ((d: DialogInterface) -> Unit),
    @StringRes confirmButtonText: Int
  ) {
    showAlert(
      context = context,
      alertIntent = AlertIntent.CONFIRM,
      message = message,
      title = title,
      confirmButtonListener = confirmButtonListener,
      confirmButtonText = confirmButtonText,
      neutralButtonListener = { d -> d.dismiss() },
      neutralButtonText = R.string.questionnaire_alert_neutral_button_title
    )
  }
}
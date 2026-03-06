package com.sg.simplekanban.commom.preferences

import android.content.Context
import android.content.SharedPreferences
import com.sg.simplekanban.R

class AppPreferences(context: Context) {

    private val sPreferences: SharedPreferences? = context.getSharedPreferences(context.getString(R.string.shared_pref_key), Context.MODE_PRIVATE)

    private fun preferenceEditor(preference: String, value: String?) {
        if (sPreferences != null) {
            val editor = sPreferences.edit()
            editor.putString(preference, value)
            editor.apply()
        }
    }

    private fun preferenceEditor(preference: String, groups: Set<String>) {
        if (sPreferences != null) {
            val editor = sPreferences.edit()
            editor.putStringSet(preference, groups)
            editor.apply()
        }
    }

    private fun preferenceEditor(preference: String, value: Int) {
        if (sPreferences != null) {
            val editor = sPreferences.edit()
            editor.putInt(preference, value)
            editor.apply()
        }
    }

    private fun preferenceEditor(preference: String, value: Long) {
        if (sPreferences != null) {
            val editor = sPreferences.edit()
            editor.putLong(preference, value)
            editor.apply()
        }
    }

    private fun preferenceEditor(preference: String, value: Boolean) {
        if (sPreferences != null) {
            val editor = sPreferences.edit()
            editor.putBoolean(preference, value)
            editor.apply()
        }
    }

    fun clearPreferences(fileIDs: Set<String?>) {
        for (file in fileIDs) {
            clearPreferences(file)
        }
    }

    private fun clearPreferences(fileID: String?) {
        sPreferences?.edit()?.remove(fileID)?.apply()
    }

    fun clearPreferences() {
        val editor = sPreferences?.edit() ?: return
        editor.clear()
        editor.apply()
    }

    fun getLastKanbanId(): String? {
        return sPreferences?.getString(PreferencesValues.LAST_KANBAN_ID, null)
    }

    fun setLastKanbanId(id: String?) {
        preferenceEditor(PreferencesValues.LAST_KANBAN_ID, id)
    }

    fun getLastKanbanUserId(): String? {
        return sPreferences?.getString(PreferencesValues.LAST_KANBAN_USER_ID, null)
    }

    fun setLastKanbanUserId(id: String?) {
        preferenceEditor(PreferencesValues.LAST_KANBAN_USER_ID, id)
    }


}
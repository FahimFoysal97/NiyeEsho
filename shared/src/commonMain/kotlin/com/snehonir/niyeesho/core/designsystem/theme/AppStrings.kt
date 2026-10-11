package com.snehonir.niyeesho.core.designsystem.theme

/**
 * Centralized app-wide UI and domain string constants for common actions, hints, labels, and error messages,
 * complying with Section 14 (Zero Hardcoded Values).
 */
object AppStrings {
    const val APP_NAME = "Niye Esho"
    const val TAGLINE_SPLASH = "Shared lists, made easy"

    const val ACTION_CANCEL = "Cancel"
    const val ACTION_DELETE = "Delete"
    const val ACTION_SAVE = "Save"
    const val ACTION_SHARE = "Share"
    const val ACTION_CLOSE = "Close"
    const val ACTION_BACK = "Back"
    const val ACTION_SEARCH = "Search"
    const val ACTION_ACCEPT = "Accept"
    const val ACTION_DECLINE = "Decline"
    const val ACTION_BLOCK = "Block"
    const val ACTION_UPDATE = "Update"
    const val ACTION_EDIT = "Edit"
    const val ACTION_COPY = "Copy"
    const val ACTION_PASTE = "Paste"
    const val ACTION_CREATE_GROUP = "Create Group"
    const val ACTION_CONNECT = "Connect"

    const val HINT_SEARCH_CONTACTS = "Search contacts..."
    const val HINT_SEARCH_LISTS = "Search lists..."
    const val HINT_PASTE_INVITE_LINK = "or paste invite link"

    const val TITLE_LISTS = "Lists"
    const val TITLE_CONTACTS = "Contacts"
    const val TITLE_INVITE = "Invite"
    const val TITLE_PROFILE = "Profile"
    const val TITLE_SETTINGS = "Settings"
    const val TITLE_DELETE_CONTACT = "Delete Contact"
    const val TITLE_LAST_ACTIVE_LIST = "LAST ACTIVE LIST"

    const val GREETING_MORNING = "Good morning"
    const val LABEL_DEFAULT_USER = "Sneho"
    const val LABEL_DEFAULT_OWNER = "Sneho Nir"
    const val LABEL_LOADING = "Loading..."
    const val LABEL_ERROR = "Something went wrong"
    const val LABEL_SUCCESS = "Success"
    const val LABEL_WANTS_TO_CONNECT = "Wants to connect"
    const val LABEL_AWAITING_APPROVAL = "Awaiting approval..."
    const val LABEL_COMPLETED = "Completed"
    const val LABEL_EMPTY_SHOPPING_LISTS = "No shopping lists yet. Tap '+' to create one!"

    const val ERROR_SIGN_IN_GOOGLE = "Unable to sign in with Google."
    const val ERROR_SIGN_IN = "Unable to sign in."
    const val ERROR_TOKEN_NOT_FOUND = "Google access token not found. Please sign in with Google to sync."
    const val ERROR_CONTACT_NOT_FOUND = "Contact not found."
    const val ERROR_FIREBASE_NO_USER = "Firebase returned no authenticated user."
    const val ERROR_LIST_NOT_FOUND = "Shopping list not found."
}

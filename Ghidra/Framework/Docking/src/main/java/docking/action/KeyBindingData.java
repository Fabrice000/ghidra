/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package docking.action;

import javax.swing.KeyStroke;

import docking.KeyBindingPrecedence;
import docking.actions.KeyBindingUtils;

/**
 * An object that contains a key stroke and the precedence for when that key stroke should be used.
 * 
 * <p>Note: this class creates key strokes that work on key {@code pressed}.  This effectively
 * normalizes all client key bindings to work on the same type of key stroke (pressed, typed or
 * released).
 */
public class KeyBindingData {
	private KeyStroke keyStroke;
	private KeyBindingPrecedence keyBindingPrecedence;

	public KeyBindingData(KeyStroke keyStroke) {
		this(keyStroke, KeyBindingPrecedence.DefaultLevel);
	}

	public KeyBindingData(char c, int modifiers) {
		this((int) Character.toUpperCase(c), modifiers);
	}

	public KeyBindingData(int keyCode, int modifiers) {
		this(KeyStroke.getKeyStroke(keyCode, modifiers));
	}

	/**
	 * Creates a key stroke from the given text.  See
	 * {@link KeyBindingUtils#parseKeyStroke(KeyStroke)}.   The key stroke created for this class
	 * will always be a key {@code pressed} key stroke.
	 * 
	 * @param keyStrokeString the key stroke string to parse
	 */
	public KeyBindingData(String keyStrokeString) {
		this(parseKeyStrokeString(keyStrokeString));
	}

	private static KeyStroke parseKeyStrokeString(String keyStrokeString) {
		KeyStroke keyStroke = KeyBindingUtils.parseKeyStroke(keyStrokeString);
		if (keyStroke == null) {
			throw new IllegalArgumentException("Invalid keystroke string: " + keyStrokeString);
		}
		return keyStroke;
	}

	public KeyBindingData(KeyStroke keyStroke, KeyBindingPrecedence precedence) {
		if (precedence == KeyBindingPrecedence.SystemActionsLevel) {
			throw new IllegalArgumentException(
				"Can't set precedence to System KeyBindingPrecedence");
		}
		this.keyStroke = keyStroke;
		this.keyBindingPrecedence = precedence;
	}

	/**
	 * Returns an accelerator keystroke to be associated with this action.
	 * @return the binding
	 */
	public KeyStroke getKeyBinding() {
		return keyStroke;
	}

	/**
	 * Returns the keyBindingPrecedence for this action
	 * @return the precedence
	 */
	public KeyBindingPrecedence getKeyBindingPrecedence() {
		return keyBindingPrecedence;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[KeyStroke=" + keyStroke + ", precedence=" +
			keyBindingPrecedence + "]";
	}

	static KeyBindingData createSystemKeyBindingData(KeyStroke keyStroke) {
		KeyBindingData keyBindingData = new KeyBindingData(keyStroke);
		keyBindingData.keyBindingPrecedence = KeyBindingPrecedence.SystemActionsLevel;
		return keyBindingData;
	}

	/**
	 * Updates the given data with system-independent versions of key modifiers.  For example,
	 * the <code>control</code> key will be converted to the <code>command</code> key on the Mac.
	 * @param newKeyBindingData the data to validate
	 * @return the potentially changed data
	 */
	static KeyBindingData validateKeyBindingData(KeyBindingData newKeyBindingData) {
		if (newKeyBindingData == null) {
			return null;
		}

		KeyStroke keyBinding = newKeyBindingData.getKeyBinding();
		if (keyBinding == null) {
			// not sure when this can happen
			return newKeyBindingData;
		}

		KeyBindingPrecedence precedence = newKeyBindingData.getKeyBindingPrecedence();
		if (precedence == KeyBindingPrecedence.SystemActionsLevel) {
			return createSystemKeyBindingData(KeyBindingUtils.validateKeyStroke(keyBinding));
		}
		return new KeyBindingData(KeyBindingUtils.validateKeyStroke(keyBinding), precedence);
	}
}

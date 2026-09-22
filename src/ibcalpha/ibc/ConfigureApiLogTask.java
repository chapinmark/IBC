// This file is part of IBC.
// Copyright (C) 2004 Steven M. Kearns (skearns23@yahoo.com )
// Copyright (C) 2004 - 2018 Richard L King (rlking@aultan.com)
// For conditions of distribution and use, see copyright notice in COPYING.txt

// IBC is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// IBC is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with IBC.  If not, see <http://www.gnu.org/licenses/>.

package ibcalpha.ibc;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

/**
 * Ticks "Create API message log file" in Configure &gt; Settings &gt; API &gt; Settings.
 *
 * Without this, nothing else in the API log export chain matters: API logging is
 * off after every container start, so the exporter would faithfully export
 * nothing. The setting lives in TWS's encrypted per-session state, NOT in
 * jts.ini (verified by byte-comparing jts.ini before and after toggling it), so
 * it cannot be baked into the image and has to be re-applied on every start.
 *
 * The label is exact for TWS 10.45.1j, from ji18n/Language.properties in
 * jts4launch-1045.jar:
 *     Create_raw_API_log = Create API message log file
 * SwingUtils.findCheckBox matches with equalsIgnoreCase, so case is not load
 * bearing, but the wording is -- IB has changed it between versions.
 *
 * CATCHES Throwable, deliberately, unlike the sibling Configure*Task classes
 * which catch only IbcException. ConfigurationTask.ConfigTaskRunner calls
 * releaseConfigDialog() AFTER t.get() with no finally block, so anything thrown
 * out of this method leaves the dialog's usage count permanently above zero.
 * DefaultConfigDialogManager.decrementUsage() then never reaches 0, the
 * "Configuration tasks completed" / click-OK path never runs, and the config
 * dialog stays open forever -- at which point TWS never opens the API port even
 * though login succeeded and "Login has completed" is in the log. Nothing this
 * class does is worth that, so it swallows everything and leaves the checkbox
 * alone on any doubt.
 */
public class ConfigureApiLogTask implements ConfigurationAction {

    static final String CHECKBOX_LABEL = "Create API message log file";

    private final boolean apiLogging;
    private JDialog configDialog;

    ConfigureApiLogTask(boolean apiLogging) {
        this.apiLogging = apiLogging;
    }

    @Override
    public void run() {
        try {
            Utils.logToConsole("Setting ApiLogging");

            Utils.selectApiSettings(configDialog);

            JCheckBox cb = SwingUtils.findCheckBox(configDialog, CHECKBOX_LABEL);
            if (cb == null) {
                // Not fatal: older/newer TWS may word this differently. Never
                // throw -- see the class comment.
                Utils.logError("could not find '" + CHECKBOX_LABEL + "' checkbox");
                return;
            }

            if (cb.isSelected() == apiLogging) {
                Utils.logToConsole("Create API message log file is already set to: " + apiLogging);
            } else {
                cb.setSelected(apiLogging);
                Utils.logToConsole("Create API message log file is now set to: " + apiLogging);
            }
        } catch (Throwable t) {
            Utils.logToConsole("ConfigureApiLogTask: failed (ignored): " + t);
        }
    }

    @Override
    public void initialise(JDialog configDialog) {
        this.configDialog = configDialog;
    }
}

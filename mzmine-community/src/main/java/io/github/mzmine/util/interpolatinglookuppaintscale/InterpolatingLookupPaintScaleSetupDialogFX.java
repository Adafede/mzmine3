/*
 * Copyright (c) 2004-2022 The MZmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.mzmine.util.interpolatinglookuppaintscale;

import io.github.mzmine.util.misc.ExitCode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;



public class InterpolatingLookupPaintScaleSetupDialogFX extends Stage {

    private InterpolatingLookupPaintScaleSetupDialogController controller;

    public InterpolatingLookupPaintScaleSetupDialogFX(InterpolatingLookupPaintScale paintScale){

        try{

            FXMLLoader root = new FXMLLoader(getClass().getResource("InterpolatingLookupPaintScaleSetupDialogFX.fxml"));
            Parent rootPane = root.load();
            Scene scene = new Scene(rootPane);
            setScene(scene);
            setMinWidth(480);
            setMinHeight(330);

            controller = root.getController();
            controller.addPaintScaleToTableView(paintScale);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }



    public ExitCode getExitCode() {
       return controller.getExitCode();
    }

    public InterpolatingLookupPaintScale getPaintScale() {

        return controller.getPaintScale();
    }

}

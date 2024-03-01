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
/*
 * author Owen Myers (Oweenm@gmail.com)
 */
package io.github.mzmine.modules.dataprocessing.featdet_chromatogramdeconvolution.ADAPpeakpicking;

import java.text.NumberFormat;
import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.impl.SimpleParameterSet;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.DoubleParameter;
import io.github.mzmine.util.misc.ExitCode;

/**
 *
 * @author owenmyers
 */
public class WaveletCoefficientsSNParameters extends SimpleParameterSet {
  public static final DoubleParameter HALF_WAVELET_WINDOW = new DoubleParameter("Peak width mult.",
      "Singal to noise estimator window size determination.", NumberFormat.getNumberInstance(), 3.0,
      0.0, null);

  public static final BooleanParameter ABS_WAV_COEFFS = new BooleanParameter("abs(wavelet coeffs.)",
      "Do you want to take the absolute value of the wavelet coefficients.", true);

  public WaveletCoefficientsSNParameters() {
    super(new Parameter[] {HALF_WAVELET_WINDOW, ABS_WAV_COEFFS});
  }

  @Override
  public ExitCode showSetupDialog(boolean valueCheckRequired) {

    final SNSetUpDialog dialog = new SNSetUpDialog(valueCheckRequired, this);
    dialog.showAndWait();
    return dialog.getExitCode();
  }

}

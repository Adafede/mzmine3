/*
 * Copyright 2006-2020 The MZmine Development Team
 * 
 * This file is part of MZmine.
 * 
 * MZmine is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.parameters.parametertypes.colorpalette;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;

public class ColorPaletteComponent extends FlowPane {

  private static final Logger logger = Logger.getLogger(ColorPaletteComponent.class.getName());

  protected SimpleColorPalette value;
  protected ComboBox<SimpleColorPalette> box;
  protected Button addPalette;
  protected Button editPalette;
  protected Button deletePalette;

  public ColorPaletteComponent() {
    super();

    box = new ComboBox<>();
    box.setMinWidth(200);
    box.setPrefWidth(200);
    box.setMaxHeight(box.getHeight());
    box.setCellFactory(p -> {
      return new ColorPaletteCell(17);
    });
    box.setButtonCell(new ColorPaletteCell(15));

    addPalette = new Button("New palette");
    addPalette.setOnAction(e -> {
      SimpleColorPalette pal = new SimpleColorPalette();
      box.getItems().add(pal);
      box.getSelectionModel().select(box.getItems().indexOf(pal));
    });

    editPalette = new Button("Edit");

    deletePalette = new Button("Delete");

    this.getChildren().addAll(box, addPalette, editPalette, deletePalette);
  }

  public SimpleColorPalette getValue() {
    return box.getValue();
  }

  public void setValue(SimpleColorPalette value) {
    if (box.getItems().indexOf(value) == -1)
      logger.warning("Value of ColorPaletteComponent was set to a value not contained "
          + "in the items. This might lead to unexpected behaviour.");
    box.setValue(value);
  }

  public List<SimpleColorPalette> getPalettes() {
    return box.getItems();
  }

  public void setPalettes(List<SimpleColorPalette> list) {
    if (list.isEmpty())
      return;

    box.getItems().clear();
    for (SimpleColorPalette p : list)
      box.getItems().add(p);
  }
}


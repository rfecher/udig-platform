/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.property.pages;

import net.refractions.udig.project.internal.Layer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Preference page for controlling Layer interaction with user.
 * 
 * @author pfeiffp
 */
public class LayerInteractionPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private Button visibleButton;
    private Button layerButton;
    private Button infromationButton;
    private Button selectButton;
    private Button editButton;
    private Button backgroundButton;
    private Button boundaryButton;
    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents( Composite parent ) {
        final Layer layer = (Layer) getElement();

        boolean isPolygon = false;
        boolean isRaster = false;
        SimpleFeatureType schema = layer.getSchema();

        // check if layer is polygon
        if (schema != null) {
            GeometryDescriptor geomDescriptor = schema.getGeometryDescriptor();
            if (geomDescriptor != null) {
                Class< ? extends Geometry> binding = (Class< ? extends Geometry>) geomDescriptor
                        .getType().getBinding();
                switch( Geometries.getForBinding(binding) ) {
                case MULTIPOLYGON:
                case POLYGON:
                    isPolygon = true;
                    break;
                default:
                }
            }
        }
        // check if raster layer
        else {
            if (layer.canAdaptTo(AbstractGridCoverage2DReader.class)) {
                isRaster = true;
            }
        }

        Composite interactionPage = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        interactionPage.setLayout(layout);

        Group generalGroup = new Group(interactionPage, SWT.SHADOW_ETCHED_IN);
        generalGroup.setText("General");

        visibleButton = new Button(generalGroup, SWT.CHECK);
        visibleButton.setText("Visible");
        visibleButton.setLocation(40, 20);
        visibleButton.pack();
        visibleButton.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent event ) {
                updateButtons();
            }
            public void widgetDefaultSelected( SelectionEvent event ) {
            }
        });

        Group toolsGroup = new Group(interactionPage, SWT.SHADOW_ETCHED_IN);
        toolsGroup.setText("Tools");

        layerButton = new Button(toolsGroup, SWT.RADIO);
        layerButton.setText("Layer");
        layerButton.setLocation(20, 20);
        layerButton.pack();

        infromationButton = new Button(toolsGroup, SWT.CHECK);
        infromationButton.setText("Information");
        infromationButton.setLocation(40, 40);
        infromationButton.pack();
        infromationButton.setEnabled(true);

        selectButton = new Button(toolsGroup, SWT.CHECK);
        selectButton.setText("Select");
        selectButton.setLocation(40, 60);
        selectButton.pack();

        editButton = new Button(toolsGroup, SWT.CHECK);
        editButton.setText("Edit");
        editButton.setLocation(40, 80);
        editButton.pack();

        backgroundButton = new Button(toolsGroup, SWT.RADIO);
        backgroundButton.setText("Background");
        backgroundButton.setLocation(20, 100);
        backgroundButton.pack();

        boundaryButton = new Button(toolsGroup, SWT.CHECK);
        boundaryButton.setText("Boundary");
        boundaryButton.setLocation(40, 120);
        boundaryButton.pack();

        loadLayer();
        return interactionPage;
    }
    @Override
    public boolean performOk() {
        saveLayer();
        return super.performOk();
    }
    @Override
    protected void performApply() {
        saveLayer();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        loadLayer();
        super.performDefaults();
    }
    
    /** Updae the apply and revert buttons if anything has been modified ... */
    protected void updateButtons(){
        final Layer layer = (Layer) getElement();
        boolean changed = visibleButton.getSelection() != layer.isVisible();
        
        this.getApplyButton().setEnabled(changed);
        this.getDefaultsButton().setEnabled(changed);
    }
    
    private void saveLayer() {
        final Layer layer = (Layer) getElement();
        if( visibleButton.getSelection() != layer.isVisible() ){
            layer.setVisible(visibleButton.getSelection());
        }
    }
    
    
    /** Grabs the layer and fills in the curren page. */
    private void loadLayer() {
        final Layer layer = (Layer) getElement();

        boolean isPolygon = false;
        boolean isRaster = false;
        SimpleFeatureType schema = layer.getSchema();

        // check if layer is polygon
        if (schema != null) {
            GeometryDescriptor geomDescriptor = schema.getGeometryDescriptor();
            if (geomDescriptor != null) {
                Class< ? extends Geometry> binding = (Class< ? extends Geometry>) geomDescriptor
                        .getType().getBinding();
                switch( Geometries.getForBinding(binding) ) {
                case MULTIPOLYGON:
                case POLYGON:
                    isPolygon = true;
                    break;
                default:
                }
            }
        }
        // check if raster layer
        else {
            if (layer.canAdaptTo(AbstractGridCoverage2DReader.class)) {
                isRaster = true;
            }
        }
        visibleButton.setSelection(layer.isVisible());
        infromationButton.setSelection(layer.isApplicable("information"));
        selectButton.setSelection(layer.isSelectable());
        editButton.setSelection(layer.isApplicable("editable"));
    }
}

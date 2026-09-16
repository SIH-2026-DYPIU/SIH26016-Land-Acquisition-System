import { useMemo, useState } from "react";
import {
  MapContainer,
  TileLayer,
  CircleMarker,
  Rectangle,
  Popup,
  useMap,
} from "react-leaflet";
import {
  Download,
  Layers3,
  MapPin,
  Search,
  LocateFixed,
  X,
} from "lucide-react";
import "leaflet/dist/leaflet.css";

const PROJECTS = [
  { id: "PRJ-001", name: "Pune Ring Road", district: "Pune", state: "Maharashtra", position: [18.52, 73.86], status: "Acquisition" },
  { id: "PRJ-002", name: "Mumbai Coastal Highway", district: "Mumbai", state: "Maharashtra", position: [19.08, 72.88], status: "Compensation" },
  { id: "PRJ-003", name: "Nagpur Metro Extension", district: "Nagpur", state: "Maharashtra", position: [21.15, 79.09], status: "Awarded" },
  { id: "PRJ-004", name: "Nashik Highway", district: "Nashik", state: "Maharashtra", position: [20.00, 73.78], status: "Possession" },
  { id: "PRJ-005", name: "Aurangabad Industrial Zone", district: "Aurangabad", state: "Maharashtra", position: [19.88, 75.34], status: "Completed" },
  { id: "PRJ-006", name: "Delhi Regional Corridor", district: "Gurugram", state: "Haryana", position: [28.46, 77.03], status: "Acquisition" },
  { id: "PRJ-007", name: "Bengaluru Peripheral Road", district: "Bengaluru", state: "Karnataka", position: [13.00, 77.60], status: "Verification" },
  { id: "PRJ-008", name: "Hyderabad Transport Corridor", district: "Hyderabad", state: "Telangana", position: [17.39, 78.49], status: "Compensation" },
  { id: "PRJ-009", name: "Kolkata Industrial Link", district: "Kolkata", state: "West Bengal", position: [22.57, 88.36], status: "Awarded" },
  { id: "PRJ-010", name: "Ahmedabad Urban Corridor", district: "Ahmedabad", state: "Gujarat", position: [23.02, 72.57], status: "Possession" },
];

const PARCELS = [
  { id: "LP-10001", position: [18.525, 73.855], status: "Acquisition", area: "4.8 Ha", village: "Wagholi", district: "Pune" },
  { id: "LP-10002", position: [18.535, 73.875], status: "Compensation", area: "2.4 Ha", village: "Kharadi", district: "Pune" },
  { id: "LP-10003", position: [18.585, 73.735], status: "Awarded", area: "6.2 Ha", village: "Hinjewadi", district: "Pune" },
  { id: "LP-10004", position: [18.565, 73.785], status: "Verification", area: "1.8 Ha", village: "Baner", district: "Pune" },
  { id: "LP-10005", position: [18.585, 73.925], status: "Possession", area: "8.1 Ha", village: "Lohegaon", district: "Pune" },
  { id: "LP-10006", position: [18.505, 73.895], status: "Acquisition", area: "3.1 Ha", village: "Hadapsar", district: "Pune" },
];

const INDIA_CENTER = [22.9734, 78.6569];
const STATUS_COLORS = {
  Acquisition: "#285985",
  Compensation: "#4c7ca5",
  Awarded: "#5e9676",
  Verification: "#c78b38",
  Possession: "#765a9b",
  Completed: "#4b8062",
};

function MapController({ focus }) {
  const map = useMap();
  if (focus) map.flyTo(focus.position, focus.zoom || 10, { duration: 0.8 });
  return null;
}

function GISMap() {
  const [selected, setSelected] = useState(null);
  const [query, setQuery] = useState("");
  const [showProjects, setShowProjects] = useState(true);
  const [showParcels, setShowParcels] = useState(true);
  const [showDistrictLayer, setShowDistrictLayer] = useState(false);
  const [focus, setFocus] = useState(null);

  const filteredProjects = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return PROJECTS;
    return PROJECTS.filter((p) =>
      [p.id, p.name, p.district, p.state].some((value) => value.toLowerCase().includes(q))
    );
  }, [query]);

  const focusSearch = () => {
    const target = filteredProjects[0];
    if (target) {
      setSelected({ type: "project", ...target });
      setFocus({ position: target.position, zoom: 11 });
    }
  };

  const locateIndia = () => {
    setSelected(null);
    setFocus({ position: INDIA_CENTER, zoom: 5 });
  };

  const exportMapData = () => {
    const payload = {
      generatedAt: new Date().toISOString(),
      note: "NLAMS prototype GIS view",
      projects: PROJECTS,
      parcels: PARCELS,
    };
    const blob = new Blob([JSON.stringify(payload, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "nlams-gis-data.json";
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <>
      <div className="page-header">
        <div>
          <div className="breadcrumb">Home / GIS Map</div>
          <h1>GIS Land Map</h1>
          <p>National spatial view of projects and land parcels.</p>
        </div>
        <button className="primary-btn" onClick={exportMapData}>
          <Download size={18} />
          Export Map Data
        </button>
      </div>

      <div className="gis-layout gis-layout-real">
        <div className="map-area real-map-area">
          <div className="real-map-toolbar">
            <div className="gis-search-control">
              <Search size={16} />
              <input
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && focusSearch()}
                placeholder="Search project, district or state..."
              />
              {query && (
                <button className="gis-clear" onClick={() => setQuery("")} aria-label="Clear search">
                  <X size={14} />
                </button>
              )}
            </div>
            <button className="gis-control-btn" onClick={focusSearch} title="Find result">
              <Search size={16} />
            </button>
            <button className="gis-control-btn" onClick={locateIndia} title="Show India">
              <LocateFixed size={16} />
            </button>
          </div>

          <div className="gis-layer-control">
            <div className="gis-layer-title"><Layers3 size={16} /> Layers</div>
            <label><input type="checkbox" checked={showProjects} onChange={(e) => setShowProjects(e.target.checked)} /> Projects</label>
            <label><input type="checkbox" checked={showParcels} onChange={(e) => setShowParcels(e.target.checked)} /> Land parcels</label>
            <label><input type="checkbox" checked={showDistrictLayer} onChange={(e) => setShowDistrictLayer(e.target.checked)} /> District overlay</label>
          </div>

          <MapContainer center={INDIA_CENTER} zoom={5} minZoom={4} maxZoom={18} scrollWheelZoom className="nlams-leaflet-map">
            <TileLayer
              attribution='&copy; OpenStreetMap contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <MapController focus={focus} />

            {showDistrictLayer && (
              <Rectangle
                bounds={[[15.6, 72.6], [21.0, 80.5]]}
                pathOptions={{ color: "#285985", weight: 1.5, fillOpacity: 0.04, dashArray: "6 5" }}
              />
            )}

            {showParcels && PARCELS.map((parcel) => (
              <CircleMarker
                key={parcel.id}
                center={parcel.position}
                radius={8}
                pathOptions={{ color: "#ffffff", weight: 2, fillColor: STATUS_COLORS[parcel.status], fillOpacity: 0.9 }}
                eventHandlers={{ click: () => setSelected({ type: "parcel", ...parcel }) }}
              >
                <Popup>
                  <strong>{parcel.id}</strong><br />
                  {parcel.village}, {parcel.district}<br />
                  {parcel.area} · {parcel.status}
                </Popup>
              </CircleMarker>
            ))}

            {showProjects && PROJECTS.map((project) => (
              <CircleMarker
                key={project.id}
                center={project.position}
                radius={query && filteredProjects.some((p) => p.id === project.id) ? 9 : 6}
                pathOptions={{ color: "#173b5d", weight: 2, fillColor: STATUS_COLORS[project.status] || "#285985", fillOpacity: 1 }}
                eventHandlers={{ click: () => {
                  setSelected({ type: "project", ...project });
                  setFocus({ position: project.position, zoom: 11 });
                } }}
              >
                <Popup>
                  <strong>{project.id} · {project.name}</strong><br />
                  {project.district}, {project.state}<br />
                  Status: {project.status}
                </Popup>
              </CircleMarker>
            ))}
          </MapContainer>

          <div className="gis-map-note">Prototype map data · Replace with approved GIS/GeoJSON layers and PostGIS coordinates for production.</div>
        </div>

        <div className="gis-sidebar panel real-gis-sidebar">
          <h3>Map Information</h3>
          <div className="map-stat"><span>Projects mapped</span><strong>{PROJECTS.length}</strong></div>
          <div className="map-stat"><span>Land parcels</span><strong>{PARCELS.length}</strong></div>
          <div className="map-stat"><span>National view</span><strong>India</strong></div>
          <hr />

          {selected ? (
            <div className="gis-selected-card">
              <div className="gis-selected-head">
                <span>{selected.type === "parcel" ? "Selected Parcel" : "Selected Project"}</span>
                <button onClick={() => setSelected(null)} aria-label="Close selected item"><X size={15} /></button>
              </div>
              <strong>{selected.id}</strong>
              <p>{selected.name || `${selected.village}, ${selected.district}`}</p>
              {selected.area && <span>Area: {selected.area}</span>}
              <span>Status: <b>{selected.status}</b></span>
              {selected.state && <span>State: {selected.state}</span>}
              <button className="popup-btn" onClick={() => setFocus({ position: selected.position, zoom: 13 })}>
                <MapPin size={14} /> Zoom to location
              </button>
            </div>
          ) : (
            <div className="gis-empty-selection">
              <MapPin size={22} />
              <strong>Select a project or parcel</strong>
              <span>Click a marker on the map to inspect its basic spatial information.</span>
            </div>
          )}

          <hr />
          <h4>Status Legend</h4>
          <div className="real-map-legend">
            {Object.entries(STATUS_COLORS).slice(0, 5).map(([status, color]) => (
              <span key={status}><i style={{ background: color }}></i>{status}</span>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}

export default GISMap;

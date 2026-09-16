import { useState, useEffect } from "react";
import { LoginPage } from "./auth/LoginPage.jsx";
import GISMap from "./GISMap.jsx";

import {
  LayoutDashboard,
  FolderKanban,
  FileText,
  Map,
  MapPin,
  Activity,
  Trophy,
  IndianRupee,
  Truck,
  Users,
  Bell,
  Search,
  ChevronDown,
  Plus,
  Download,
  CheckCircle2,
  Clock3,
  AlertCircle,
  XCircle,
  ArrowUpRight,
  ArrowDownRight,
  Menu,
  X,
  Eye,
  Edit3,
  Trash2,
  Filter,
  MoreHorizontal,
  Building2,
  LandPlot,
  ShieldCheck,
} from "lucide-react";

import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
} from "recharts";


/* =========================
   DATA
========================= */

const menuItems = [
  {
    title: "MAIN MENU",
    items: [
      { id: "dashboard", label: "Dashboard", icon: LayoutDashboard },
      { id: "projects", label: "Projects", icon: FolderKanban },
      { id: "proposals", label: "Proposals", icon: FileText, badge: 8 },
      { id: "parcels", label: "Land Parcels", icon: Map },
      { id: "gis", label: "GIS Map", icon: MapPin },
    ],
  },
  {
    title: "ACQUISITION",
    items: [
      { id: "acquisition", label: "Acquisition", icon: Activity },
      { id: "awards", label: "Awards", icon: Trophy },
      { id: "compensation", label: "Compensation", icon: IndianRupee },
      { id: "possession", label: "Possession", icon: Truck },
      {
        id: "rehabilitation",
        label: "Rehabilitation & Resettlement",
        icon: Users,
      },
    ],
  },
];

const projectsData = [
  {
    id: "PRJ-001",
    name: "Pune Ring Road",
    district: "Pune",
    parcels: 1240,
    stage: "Acquisition",
    progress: 72,
    status: "Active",
    budget: "₹245 Cr",
  },
  {
    id: "PRJ-002",
    name: "Mumbai Coastal Highway",
    district: "Mumbai",
    parcels: 856,
    stage: "Compensation",
    progress: 86,
    status: "Active",
    budget: "₹410 Cr",
  },
  {
    id: "PRJ-003",
    name: "Nagpur Metro Extension",
    district: "Nagpur",
    parcels: 642,
    stage: "Award",
    progress: 58,
    status: "Active",
    budget: "₹180 Cr",
  },
  {
    id: "PRJ-004",
    name: "Nashik Highway",
    district: "Nashik",
    parcels: 432,
    stage: "Possession",
    progress: 91,
    status: "Active",
    budget: "₹120 Cr",
  },
  {
    id: "PRJ-005",
    name: "Aurangabad Industrial Zone",
    district: "Aurangabad",
    parcels: 318,
    stage: "Completed",
    progress: 100,
    status: "Completed",
    budget: "₹95 Cr",
  },
];

const proposalsData = [
  {
    id: "PROP-1001",
    project: "Pune Ring Road",
    district: "Pune",
    submitted: "16 Sep 2026",
    amount: "₹4.2 Cr",
    status: "Pending",
  },
  {
    id: "PROP-1002",
    project: "Mumbai Coastal Highway",
    district: "Mumbai",
    submitted: "15 Sep 2026",
    amount: "₹8.7 Cr",
    status: "Pending",
  },
  {
    id: "PROP-1003",
    project: "Nagpur Metro Extension",
    district: "Nagpur",
    submitted: "14 Sep 2026",
    amount: "₹3.5 Cr",
    status: "Approved",
  },
  {
    id: "PROP-1004",
    project: "Nashik Highway",
    district: "Nashik",
    submitted: "12 Sep 2026",
    amount: "₹2.1 Cr",
    status: "Rejected",
  },
];

const parcelsData = [
  {
    id: "LP-10001",
    village: "Wagholi",
    district: "Pune",
    area: "4.8 Ha",
    owner: "Registered",
    status: "Under Acquisition",
  },
  {
    id: "LP-10002",
    village: "Kharadi",
    district: "Pune",
    area: "2.4 Ha",
    owner: "Verified",
    status: "Compensation",
  },
  {
    id: "LP-10003",
    village: "Hinjewadi",
    district: "Pune",
    area: "6.2 Ha",
    owner: "Registered",
    status: "Awarded",
  },
  {
    id: "LP-10004",
    village: "Baner",
    district: "Pune",
    area: "1.8 Ha",
    owner: "Pending",
    status: "Verification",
  },
  {
    id: "LP-10005",
    village: "Lohegaon",
    district: "Pune",
    area: "8.1 Ha",
    owner: "Verified",
    status: "Possession",
  },
];

const acquisitionData = [
  { month: "Apr", acquisition: 38 },
  { month: "May", acquisition: 45 },
  { month: "Jun", acquisition: 51 },
  { month: "Jul", acquisition: 58 },
  { month: "Aug", acquisition: 66 },
  { month: "Sep", acquisition: 76 },
];

const statusData = [
  { name: "Acquisition", value: 42 },
  { name: "Compensation", value: 24 },
  { name: "Awarded", value: 18 },
  { name: "Possession", value: 16 },
];

const monthlyProjects = [
  { month: "Apr", projects: 82 },
  { month: "May", projects: 95 },
  { month: "Jun", projects: 103 },
  { month: "Jul", projects: 110 },
  { month: "Aug", projects: 118 },
  { month: "Sep", projects: 128 },
];


/* =========================
   SMALL COMPONENTS
========================= */

function StatCard({
  title,
  value,
  subtitle,
  change,
  icon: Icon,
  negative = false,
}) {
  return (
    <div className="stat-card">
      <div className="stat-top">
        <div>
          <p>{title}</p>
          <h2>{value}</h2>
          <span>{subtitle}</span>
        </div>

        <div className="stat-icon">
          <Icon size={22} />
        </div>
      </div>

      <div className={negative ? "stat-change negative" : "stat-change"}>
        {negative ? (
          <ArrowDownRight size={16} />
        ) : (
          <ArrowUpRight size={16} />
        )}
        {change}
      </div>
    </div>
  );
}


function StatusBadge({ status }) {
  const type = status.toLowerCase().replaceAll(" ", "-");

  return (
    <span className={`status-badge ${type}`}>
      {status === "Approved" && <CheckCircle2 size={14} />}
      {status === "Pending" && <Clock3 size={14} />}
      {status === "Rejected" && <XCircle size={14} />}
      {status}
    </span>
  );
}


/* =========================
   DASHBOARD
========================= */

function Dashboard({ setActivePage }) {
  return (
    <>
      <div className="page-header">
        <div>
          <div className="breadcrumb">Home / Dashboard</div>
          <h1>Dashboard</h1>
          <p>Real-time overview of land acquisition activities.</p>
        </div>

        <button className="primary-btn">
          <Download size={18} />
          Generate Report
        </button>
      </div>

      <div className="scope-card">
        <div className="scope-icon">
          <Building2 size={21} />
        </div>

        <div>
          <small>Current administrative scope</small>
          <strong>Maharashtra State</strong>
        </div>

        <div className="scope-right">
          <span>Level: State</span>
          <span>Last updated: 2 min ago</span>
          <span className="live">
            <i></i> Live
          </span>
        </div>
      </div>


      <div className="stats-grid">
        <StatCard
          title="Active Projects"
          value="128"
          subtitle="Across Maharashtra"
          change="+8.4% vs previous period"
          icon={FolderKanban}
        />

        <StatCard
          title="Land Parcels"
          value="12,540"
          subtitle="Under acquisition"
          change="+4.2% vs previous period"
          icon={LandPlot}
        />

        <StatCard
          title="Pending Actions"
          value="24"
          subtitle="Require your attention"
          change="6 urgent"
          icon={AlertCircle}
          negative
        />

        <StatCard
          title="Completed Projects"
          value="37"
          subtitle="This financial year"
          change="+12.6% vs previous period"
          icon={CheckCircle2}
        />
      </div>


      <div className="charts-grid">

        <div className="panel large-panel">
          <div className="panel-header">
            <div>
              <h3>Acquisition Stage Progress</h3>
              <p>Monthly progress across active projects</p>
            </div>

            <button className="icon-btn">
              <MoreHorizontal size={19} />
            </button>
          </div>

          <div className="chart-container">
            <ResponsiveContainer width="100%" height={320}>
              <BarChart data={acquisitionData}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Bar
                  dataKey="acquisition"
                  fill="#285985"
                  radius={[6, 6, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>


        <div className="panel">
          <div className="panel-header">
            <div>
              <h3>Project Status</h3>
              <p>Current project distribution</p>
            </div>
          </div>

          <div className="pie-container">
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie
                  data={statusData}
                  dataKey="value"
                  nameKey="name"
                  innerRadius={70}
                  outerRadius={100}
                  paddingAngle={3}
                >
                  {statusData.map((_, index) => (
                    <Cell
                      key={index}
                      fill={
                        ["#285985", "#4c7ca5", "#5e9676", "#d7a348"][
                          index
                        ]
                      }
                    />
                  ))}
                </Pie>

                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          <div className="legend">
            {statusData.map((item, index) => (
              <div className="legend-item" key={item.name}>
                <span
                  className="legend-dot"
                  style={{
                    background: [
                      "#285985",
                      "#4c7ca5",
                      "#5e9676",
                      "#d7a348",
                    ][index],
                  }}
                ></span>

                <span>{item.name}</span>

                <strong>{item.value}%</strong>
              </div>
            ))}
          </div>
        </div>
      </div>


      <div className="bottom-grid">

        <div className="panel">
          <div className="panel-header">
            <div>
              <h3>Project Growth</h3>
              <p>Active projects over the last 6 months</p>
            </div>
          </div>

          <ResponsiveContainer width="100%" height={240}>
            <LineChart data={monthlyProjects}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Line
                type="monotone"
                dataKey="projects"
                stroke="#285985"
                strokeWidth={3}
                dot={{ r: 4 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>


        <div className="panel quick-panel">
          <div className="panel-header">
            <div>
              <h3>Quick Actions</h3>
              <p>Frequently used operations</p>
            </div>
          </div>

          <button onClick={() => setActivePage("projects")}>
            <Plus size={18} />
            Create New Project
          </button>

          <button onClick={() => setActivePage("proposals")}>
            <FileText size={18} />
            Review Proposals
          </button>

          <button onClick={() => setActivePage("parcels")}>
            <Map size={18} />
            Search Land Parcels
          </button>

          <button onClick={() => setActivePage("gis")}>
            <MapPin size={18} />
            Open GIS Map
          </button>
        </div>

      </div>
    </>
  );
}


/* =========================
   PROJECTS
========================= */

function Projects() {
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);

  const filtered = projectsData.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.district.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <>
      <div className="page-header">
        <div>
          <div className="breadcrumb">Home / Projects</div>
          <h1>Projects</h1>
          <p>Manage and monitor land acquisition projects.</p>
        </div>

        <button
          className="primary-btn"
          onClick={() => setShowModal(true)}
        >
          <Plus size={18} />
          New Project
        </button>
      </div>

      <div className="toolbar">
        <div className="search-box">
          <Search size={18} />
          <input
            placeholder="Search projects..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <button className="secondary-btn">
          <Filter size={17} />
          Filter
        </button>
      </div>

      <div className="panel table-panel">
        <div className="panel-header">
          <div>
            <h3>All Projects</h3>
            <p>{filtered.length} projects found</p>
          </div>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Project</th>
                <th>District</th>
                <th>Parcels</th>
                <th>Stage</th>
                <th>Progress</th>
                <th>Budget</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map((project) => (
                <tr key={project.id}>
                  <td>
                    <strong>{project.name}</strong>
                    <small>{project.id}</small>
                  </td>

                  <td>{project.district}</td>

                  <td>{project.parcels}</td>

                  <td>{project.stage}</td>

                  <td>
                    <div className="progress-cell">
                      <div className="progress">
                        <span
                          style={{
                            width: `${project.progress}%`,
                          }}
                        ></span>
                      </div>
                      <small>{project.progress}%</small>
                    </div>
                  </td>

                  <td>{project.budget}</td>

                  <td>
                    <StatusBadge status={project.status} />
                  </td>

                  <td>
                    <button className="small-icon">
                      <Eye size={16} />
                    </button>

                    <button className="small-icon">
                      <Edit3 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <Modal
          title="Create New Project"
          close={() => setShowModal(false)}
        />
      )}
    </>
  );
}


/* =========================
   PROPOSALS
========================= */

function Proposals() {
  const [proposals, setProposals] = useState(proposalsData);

  const updateStatus = (id, status) => {
    setProposals((old) =>
      old.map((item) =>
        item.id === id ? { ...item, status } : item
      )
    );
  };

  return (
    <>
      <div className="page-header">
        <div>
          <div className="breadcrumb">Home / Proposals</div>
          <h1>Proposals</h1>
          <p>Review and process land acquisition proposals.</p>
        </div>

        <button className="primary-btn">
          <Plus size={18} />
          New Proposal
        </button>
      </div>

      <div className="stats-grid mini-stats">
        <StatCard
          title="Total Proposals"
          value="86"
          subtitle="This financial year"
          change="+14.2%"
          icon={FileText}
        />

        <StatCard
          title="Pending"
          value="8"
          subtitle="Need review"
          change="3 urgent"
          icon={Clock3}
          negative
        />

        <StatCard
          title="Approved"
          value="61"
          subtitle="Processed"
          change="+18.4%"
          icon={CheckCircle2}
        />

        <StatCard
          title="Rejected"
          value="17"
          subtitle="This financial year"
          change="-4.1%"
          icon={XCircle}
        />
      </div>

      <div className="panel table-panel">
        <div className="panel-header">
          <div>
            <h3>Proposal Queue</h3>
            <p>Latest submitted proposals</p>
          </div>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Proposal ID</th>
                <th>Project</th>
                <th>District</th>
                <th>Submitted</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {proposals.map((proposal) => (
                <tr key={proposal.id}>
                  <td>
                    <strong>{proposal.id}</strong>
                  </td>

                  <td>{proposal.project}</td>
                  <td>{proposal.district}</td>
                  <td>{proposal.submitted}</td>
                  <td>{proposal.amount}</td>

                  <td>
                    <StatusBadge status={proposal.status} />
                  </td>

                  <td>
                    {proposal.status === "Pending" ? (
                      <>
                        <button
                          className="approve-btn"
                          onClick={() =>
                            updateStatus(
                              proposal.id,
                              "Approved"
                            )
                          }
                        >
                          Approve
                        </button>

                        <button
                          className="reject-btn"
                          onClick={() =>
                            updateStatus(
                              proposal.id,
                              "Rejected"
                            )
                          }
                        >
                          Reject
                        </button>
                      </>
                    ) : (
                      <button className="small-icon">
                        <Eye size={16} />
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}


/* =========================
   PARCELS
========================= */

function Parcels() {
  const [search, setSearch] = useState("");

  const filtered = parcelsData.filter(
    (parcel) =>
      parcel.id.toLowerCase().includes(search.toLowerCase()) ||
      parcel.village.toLowerCase().includes(search.toLowerCase()) ||
      parcel.district.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <>
      <div className="page-header">
        <div>
          <div className="breadcrumb">Home / Land Parcels</div>
          <h1>Land Parcels</h1>
          <p>Track individual land parcels and ownership status.</p>
        </div>

        <button className="primary-btn">
          <Plus size={18} />
          Register Parcel
        </button>
      </div>

      <div className="toolbar">
        <div className="search-box">
          <Search size={18} />

          <input
            placeholder="Search parcel, village or district..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <button className="secondary-btn">
          <Filter size={17} />
          Filter Status
        </button>
      </div>

      <div className="panel table-panel">
        <div className="panel-header">
          <div>
            <h3>Registered Land Parcels</h3>
            <p>Showing {filtered.length} records</p>
          </div>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Parcel ID</th>
                <th>Village</th>
                <th>District</th>
                <th>Area</th>
                <th>Ownership</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map((parcel) => (
                <tr key={parcel.id}>
                  <td>
                    <strong>{parcel.id}</strong>
                  </td>

                  <td>{parcel.village}</td>
                  <td>{parcel.district}</td>
                  <td>{parcel.area}</td>
                  <td>{parcel.owner}</td>

                  <td>
                    <StatusBadge status={parcel.status} />
                  </td>

                  <td>
                    <button className="small-icon">
                      <Eye size={16} />
                    </button>

                    <button className="small-icon">
                      <Edit3 size={16} />
                    </button>

                    <button className="small-icon danger">
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}


/* =========================
   GIS MAP
========================= */

/* GIS map is implemented in a dedicated Leaflet component. */

/* =========================
   GENERIC MODULE
========================= */

function ModulePage({ title, icon: Icon, description }) {
  const [items, setItems] = useState([
    {
      id: "TX-001",
      activity: "Processing",
      district: "Pune",
      amount: "₹2.8 Cr",
      status: "Active",
    },
    {
      id: "TX-002",
      activity: "Verification",
      district: "Mumbai",
      amount: "₹4.2 Cr",
      status: "Pending",
    },
    {
      id: "TX-003",
      activity: "Completed",
      district: "Nagpur",
      amount: "₹1.9 Cr",
      status: "Approved",
    },
  ]);

  return (
    <>
      <div className="page-header">
        <div>
          <div className="breadcrumb">Home / {title}</div>

          <h1>
            <Icon size={30} />
            {title}
          </h1>

          <p>{description}</p>
        </div>

        <button
          className="primary-btn"
          onClick={() =>
            setItems([
              ...items,
              {
                id: `TX-${items.length + 4}`,
                activity: "New Request",
                district: "Pune",
                amount: "₹1.2 Cr",
                status: "Pending",
              },
            ])
          }
        >
          <Plus size={18} />
          New Request
        </button>
      </div>

      <div className="stats-grid">
        <StatCard
          title="Total Records"
          value={items.length + 120}
          subtitle="Current period"
          change="+8.2%"
          icon={FileText}
        />

        <StatCard
          title="Pending"
          value="18"
          subtitle="Requires action"
          change="5 urgent"
          icon={Clock3}
          negative
        />

        <StatCard
          title="Processed"
          value="104"
          subtitle="Completed"
          change="+12.4%"
          icon={CheckCircle2}
        />

        <StatCard
          title="Value"
          value="₹82 Cr"
          subtitle="Current portfolio"
          change="+6.8%"
          icon={IndianRupee}
        />
      </div>

      <div className="panel table-panel">
        <div className="panel-header">
          <div>
            <h3>{title} Records</h3>
            <p>Operational records and recent activities</p>
          </div>

          <button className="secondary-btn">
            <Filter size={16} />
            Filter
          </button>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Reference</th>
                <th>Activity</th>
                <th>District</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  <td>
                    <strong>{item.id}</strong>
                  </td>

                  <td>{item.activity}</td>

                  <td>{item.district}</td>

                  <td>{item.amount}</td>

                  <td>
                    <StatusBadge
                      status={
                        item.status === "Active"
                          ? "Approved"
                          : item.status
                      }
                    />
                  </td>

                  <td>
                    <button className="small-icon">
                      <Eye size={16} />
                    </button>

                    <button className="small-icon">
                      <Edit3 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}


/* =========================
   MODAL
========================= */

function Modal({ title, close }) {
  const [submitted, setSubmitted] = useState(false);

  return (
    <div className="modal-overlay">
      <div className="modal">

        <div className="modal-header">
          <h2>{title}</h2>

          <button onClick={close}>
            <X size={20} />
          </button>
        </div>

        {!submitted ? (
          <>
            <label>Project Name</label>
            <input placeholder="Enter project name" />

            <label>District</label>

            <select>
              <option>Pune</option>
              <option>Mumbai</option>
              <option>Nagpur</option>
              <option>Nashik</option>
              <option>Aurangabad</option>
            </select>

            <label>Estimated Budget</label>
            <input placeholder="₹ 0.00 Cr" />

            <label>Description</label>
            <textarea placeholder="Project description"></textarea>

            <div className="modal-actions">
              <button
                className="secondary-btn"
                onClick={close}
              >
                Cancel
              </button>

              <button
                className="primary-btn"
                onClick={() => setSubmitted(true)}
              >
                <CheckCircle2 size={17} />
                Create Project
              </button>
            </div>
          </>
        ) : (
          <div className="success-message">
            <CheckCircle2 size={50} />

            <h3>Project Created</h3>

            <p>
              The new project has been successfully added
              to the system.
            </p>

            <button className="primary-btn" onClick={close}>
              Continue
            </button>
          </div>
        )}

      </div>
    </div>
  );
}


/* =========================
   APP
========================= */

function SecureApp({ onLogout }) {
  const [activePage, setActivePage] = useState("dashboard");
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);

  const renderPage = () => {
    switch (activePage) {
      case "dashboard":
        return <Dashboard setActivePage={setActivePage} />;

      case "projects":
        return <Projects />;

      case "proposals":
        return <Proposals />;

      case "parcels":
        return <Parcels />;

      case "gis":
        return <GISMap />;

      case "acquisition":
        return (
          <ModulePage
            title="Acquisition"
            icon={Activity}
            description="Monitor and manage land acquisition activities."
          />
        );

      case "awards":
        return (
          <ModulePage
            title="Awards"
            icon={Trophy}
            description="Manage acquisition awards and notifications."
          />
        );

      case "compensation":
        return (
          <ModulePage
            title="Compensation"
            icon={IndianRupee}
            description="Track compensation assessment and payments."
          />
        );

      case "possession":
        return (
          <ModulePage
            title="Possession"
            icon={Truck}
            description="Monitor land possession and handover activities."
          />
        );

      case "rehabilitation":
        return (
          <ModulePage
            title="Rehabilitation & Resettlement"
            icon={Users}
            description="Manage rehabilitation and resettlement cases."
          />
        );

      default:
        return <Dashboard setActivePage={setActivePage} />;
    }
  };


  return (
    <div className="app">

      {/* TOP GOVERNMENT BAR */}

      <div className="gov-bar">
        <span>🏛️ Government of India</span>

        <div>
          <span>Skip to main content</span>
          <span>Accessibility</span>
          <span>English</span>
        </div>
      </div>


      {/* HEADER */}

      <header className="header">

        <button
          className="mobile-menu"
          onClick={() => setSidebarOpen(!sidebarOpen)}
        >
          <Menu size={22} />
        </button>

        <div className="brand">
          <div className="brand-logo">
            <ShieldCheck size={28} />
          </div>

          <div>
            <small>NATIONAL LAND MANAGEMENT</small>
            <h2>Land Acquisition & Management System</h2>
            <p>Real-Time National Monitoring & Decision Support</p>
          </div>
        </div>


        <div className="header-actions">

          <button
            className="notification-btn"
            onClick={() =>
              setNotificationsOpen(!notificationsOpen)
            }
          >
            <Bell size={21} />
            <i></i>
          </button>


          <div className="profile-wrapper">

            <button
              className="profile"
              onClick={() =>
                setProfileOpen(!profileOpen)
              }
            >
              <div className="avatar">GO</div>

              <div>
                <strong>Government Officer</strong>
                <span>Maharashtra · State</span>
              </div>

              <ChevronDown size={17} />
            </button>


            {profileOpen && (
              <div className="dropdown">

                <strong>Government Officer</strong>

                <button>Profile</button>
                <button>Account Settings</button>
                <button onClick={onLogout}>Sign Out</button>

              </div>
            )}

          </div>


          {notificationsOpen && (
            <div className="notification-panel">

              <div className="notification-header">
                <strong>Notifications</strong>

                <span>3 new</span>
              </div>

              <div className="notification">
                <AlertCircle size={18} />
                <div>
                  <strong>6 urgent actions</strong>
                  <p>Require your attention.</p>
                </div>
              </div>

              <div className="notification">
                <FileText size={18} />
                <div>
                  <strong>8 proposals pending</strong>
                  <p>Review submitted proposals.</p>
                </div>
              </div>

              <div className="notification">
                <CheckCircle2 size={18} />
                <div>
                  <strong>Project completed</strong>
                  <p>Aurangabad Industrial Zone.</p>
                </div>
              </div>

            </div>
          )}

        </div>
      </header>


      {/* BODY */}

      <div className="body">

        <aside className={sidebarOpen ? "sidebar open" : "sidebar"}>

          <div className="sidebar-close">
            <button onClick={() => setSidebarOpen(false)}>
              <X size={20} />
            </button>
          </div>

          {menuItems.map((section) => (
            <div className="menu-section" key={section.title}>

              <span className="menu-title">
                {section.title}
              </span>

              {section.items.map((item) => {

                const Icon = item.icon;

                return (
                  <button
                    key={item.id}
                    className={
                      activePage === item.id
                        ? "menu-item active"
                        : "menu-item"
                    }
                    onClick={() => {
                      setActivePage(item.id);
                      setSidebarOpen(false);
                    }}
                  >
                    <Icon size={19} />

                    <span>{item.label}</span>

                    {item.badge && (
                      <b>{item.badge}</b>
                    )}
                  </button>
                );
              })}

            </div>
          ))}

        </aside>


        <main className="main">
          {renderPage()}
        </main>

      </div>


      {/* FOOTER */}

      <footer>
        <span>© 2026 National Land Management System</span>
        <span>Government of India · Maharashtra State</span>
      </footer>

    </div>
  );
}

function App() {
  const [authenticated, setAuthenticated] = useState(() => {
    return sessionStorage.getItem("nlams_authenticated") === "true";
  });

  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    // In production this check should call the backend session endpoint.
    // For the SIH frontend prototype, sessionStorage keeps the browser session
    // locked after refresh and is cleared on sign-out.
    const timer = window.setTimeout(() => setAuthLoading(false), 250);
    return () => window.clearTimeout(timer);
  }, []);

  const handleAuthenticated = () => {
    sessionStorage.setItem("nlams_authenticated", "true");
    setAuthenticated(true);
  };

  const handleLogout = () => {
    sessionStorage.removeItem("nlams_authenticated");
    setAuthenticated(false);
  };

  if (authLoading) {
    return <div className="auth-loading">Loading secure portal...</div>;
  }

  if (!authenticated) {
    return <LoginPage onAuthenticated={handleAuthenticated} />;
  }

  return <SecureApp onLogout={handleLogout} />;
}

export default App;
import pandas as pd

projects = [
    {
        "name": "Pune-Satara Highway",
        "days_pending": 43,
        "compensation_pending": 12,
        "ownership_disputes": 3,
        "approval_delay": 18,
        "rr_pending": 7,
        "acquisition_percent": 68
    },

    {
        "name": "Mumbai-Nashik Highway",
        "days_pending": 20,
        "compensation_pending": 5,
        "ownership_disputes": 1,
        "approval_delay": 8,
        "rr_pending": 2,
        "acquisition_percent": 85
    },

    {
        "name": "Nagpur Ring Road",
        "days_pending": 75,
        "compensation_pending": 25,
        "ownership_disputes": 7,
        "approval_delay": 35,
        "rr_pending": 12,
        "acquisition_percent": 45
    }
]

df = pd.DataFrame(projects)

print(df)

print("\n--- Days Pending ---")
print(df["days_pending"])

print("\n--- Average Days Pending ---")
print(df["days_pending"].mean())

print("\n--- Average Acquisition ---")
print(df["acquisition_percent"].mean())

print("\n--- Delayed Projects ---")
delayed_projects = df[df["days_pending"] > 30]
print(delayed_projects)

print("\n--- Low Acquisition Projects ---")
low_acquisition = df[df["acquisition_percent"] < 70]
print(low_acquisition)

print("\n--- Projects Sorted by Days Pending ---")
sorted_projects = df.sort_values("days_pending", ascending=False)
print(sorted_projects[["name", "days_pending"]])

print("\n--- High Priority Projects ---")

high_priority = df[
    (df["days_pending"] > 30) &
    (df["acquisition_percent"] < 70)
]

print(high_priority[["name", "days_pending", "acquisition_percent"]])



def calculate_risk(row):
    score = 0

    if row["days_pending"] > 60:
        score += 25
    elif row["days_pending"] > 30:
        score += 15

    if row["compensation_pending"] > 20:
        score += 20
    elif row["compensation_pending"] > 10:
        score += 15

    if row["ownership_disputes"] > 5:
        score += 20
    elif row["ownership_disputes"] > 2:
        score += 15
    elif row["ownership_disputes"] > 0:
        score += 10

    if row["approval_delay"] > 30:
        score += 15
    elif row["approval_delay"] > 15:
        score += 10

    if row["rr_pending"] > 10:
        score += 10
    elif row["rr_pending"] > 5:
        score += 7

    if row["acquisition_percent"] < 40:
        score += 15
    elif row["acquisition_percent"] < 70:
        score += 10

    return score


df["risk_score"] = df.apply(calculate_risk, axis=1)

print("\n--- Risk Scores ---")
print(df[["name", "risk_score"]])



def get_risk_level(score):
    if score >= 60:
        return "HIGH"
    elif score >= 30:
        return "MEDIUM"
    else:
        return "LOW"


df["risk_level"] = df["risk_score"].apply(get_risk_level)

print("\n--- Risk Assessment ---")
print(df[["name", "risk_score", "risk_level"]])



def get_reasons(row):
    reasons = []

    if row["days_pending"] > 60:
        reasons.append("Project is significantly delayed")
    elif row["days_pending"] > 30:
        reasons.append("Project has been pending for a long time")

    if row["compensation_pending"] > 20:
        reasons.append("Very high compensation backlog")
    elif row["compensation_pending"] > 10:
        reasons.append("High compensation backlog")

    if row["ownership_disputes"] > 5:
        reasons.append("Very high number of ownership disputes")
    elif row["ownership_disputes"] > 2:
        reasons.append("High number of ownership disputes")

    if row["approval_delay"] > 30:
        reasons.append("Approval process is significantly delayed")
    elif row["approval_delay"] > 15:
        reasons.append("Approval process is delayed")

    if row["rr_pending"] > 10:
        reasons.append("Very high number of pending R&R cases")
    elif row["rr_pending"] > 5:
        reasons.append("Several R&R cases are pending")

    if row["acquisition_percent"] < 40:
        reasons.append("Land acquisition progress is very low")
    elif row["acquisition_percent"] < 70:
        reasons.append("Land acquisition progress is below target")

    return reasons


df["reasons"] = df.apply(get_reasons, axis=1)

print("\n--- Risk Reasons ---")

for index, row in df.iterrows():
    print("\nProject:", row["name"])
    print("Risk:", row["risk_level"])
    print("Reasons:")

    for reason in row["reasons"]:
        print("-", reason)




def get_recommendations(row):
    recommendations = []

    if row["compensation_pending"] > 10:
        recommendations.append(
            "Prioritize compensation verification and payment"
        )

    if row["ownership_disputes"] > 2:
        recommendations.append(
            "Escalate ownership disputes for legal review"
        )

    if row["approval_delay"] > 15:
        recommendations.append(
            "Escalate the delayed approval process"
        )

    if row["rr_pending"] > 5:
        recommendations.append(
            "Prioritize pending R&R cases"
        )

    if row["days_pending"] > 30 and row["acquisition_percent"] < 70:
        recommendations.append(
            "Consider immediate intervention to improve acquisition progress"
        )

    return recommendations


df["recommendations"] = df.apply(get_recommendations, axis=1)

print("\n--- Recommendations ---")

for index, row in df.iterrows():
    print("\nProject:", row["name"])
    print("Risk:", row["risk_level"])
    print("Recommended Actions:")

    for recommendation in row["recommendations"]:
        print("-", recommendation)
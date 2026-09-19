

from pathlib import Path

import pandas as pd





# ============================================================

# 1. LOAD DATASET

# ============================================================



print("\n" + "=" * 60)

print("LAND ACQUISITION ANALYTICS SYSTEM")

print("=" * 60)





# Get the directory containing this Python file

CURRENT_DIR = Path(__file__).resolve().parent



# Move one level up to the ai folder

AI_DIR = CURRENT_DIR.parent



# Locate the CSV file

csv_file = AI_DIR / "SIH26016_Land_Acquisition_Synthetic_Data.csv"





# Load the dataset

try:

    df = pd.read_csv(csv_file)



    print("\nDataset loaded successfully!")

    print("Dataset path:", csv_file)

    print("Dataset shape:", df.shape)



except FileNotFoundError:

    print("\nCSV file not found.")

    print("Make sure the CSV file is inside the ai folder.")

    raise SystemExit





# ============================================================

# 2. DATASET OVERVIEW

# ============================================================



print("\n" + "=" * 60)

print("DATASET OVERVIEW")

print("=" * 60)



print("\n--- FIRST 5 RECORDS ---")

print(df.head())



print("\n--- DATASET INFORMATION ---")

df.info()



print("\n--- MISSING VALUES ---")

print(df.isnull().sum())





# ============================================================

# 3. DATA PREPARATION

# ============================================================



print("\n" + "=" * 60)

print("DATA PREPARATION")

print("=" * 60)





# Convert numeric columns safely

numeric_columns = [

    "land_area_acres",

    "owners_count",

    "valuation_amount",

    "compensation_amount",

    "payment_delay_days",

    "approval_delay_days",

    "acquisition_percent",

    "total_delay_days",

    "expected_completion_days"

]



for column in numeric_columns:

    if column in df.columns:

        df[column] = pd.to_numeric(

            df[column],

            errors="coerce"

        ).fillna(0)





# Convert text columns to lowercase for consistent comparisons

text_columns = [

    "ownership_dispute",

    "dispute_level",

    "consent_status",

    "compensation_status",

    "approval_status",

    "rehabilitation_status",

    "grievance_status",

    "acquisition_status"

]



for column in text_columns:

    if column in df.columns:

        df[column] = (

            df[column]

            .fillna("Unknown")

            .astype(str)

            .str.strip()

            .str.lower()

        )





# Create a display name for each land acquisition record

df["project_name"] = (

    df["state"].astype(str)

    + " - "

    + df["district"].astype(str)

    + " - "

    + df["land_id"].astype(str)

)





# ============================================================

# 4. BASIC ANALYTICS

# ============================================================



print("\n" + "=" * 60)

print("BASIC ANALYTICS")

print("=" * 60)





# Average delay

average_total_delay = df["total_delay_days"].mean()



# Average acquisition progress

average_acquisition = df["acquisition_percent"].mean()



# Average payment delay

average_payment_delay = df["payment_delay_days"].mean()



# Average approval delay

average_approval_delay = df["approval_delay_days"].mean()





print("\n--- AVERAGE TOTAL DELAY ---")

print(f"{average_total_delay:.2f} days")



print("\n--- AVERAGE ACQUISITION PROGRESS ---")

print(f"{average_acquisition:.2f}%")



print("\n--- AVERAGE PAYMENT DELAY ---")

print(f"{average_payment_delay:.2f} days")



print("\n--- AVERAGE APPROVAL DELAY ---")

print(f"{average_approval_delay:.2f} days")





# ============================================================

# 5. DELAYED PROJECTS

# ============================================================



print("\n" + "=" * 60)

print("DELAYED PROJECTS")

print("=" * 60)





# A record is considered delayed when total delay is greater than 30 days

delayed_projects = df[

    df["total_delay_days"] > 30

]





print("\nTotal delayed records:", len(delayed_projects))



if len(delayed_projects) > 0:

    print(

        delayed_projects[

            [

                "project_name",

                "total_delay_days",

                "acquisition_percent"

            ]

        ].to_string(index=False)

    )



else:

    print("No delayed records found.")





# ============================================================

# 6. LOW ACQUISITION PROJECTS

# ============================================================



print("\n" + "=" * 60)

print("LOW ACQUISITION PROJECTS")

print("=" * 60)





# Records below 70% acquisition progress are flagged

low_acquisition = df[

    df["acquisition_percent"] < 70

]





print("\nTotal low-acquisition records:", len(low_acquisition))



if len(low_acquisition) > 0:

    print(

        low_acquisition[

            [

                "project_name",

                "acquisition_percent",

                "total_delay_days"

            ]

        ].to_string(index=False)

    )



else:

    print("No low-acquisition records found.")





# ============================================================

# 7. PROJECTS SORTED BY DELAY

# ============================================================



print("\n" + "=" * 60)

print("PROJECTS SORTED BY TOTAL DELAY")

print("=" * 60)





sorted_projects = df.sort_values(

    by="total_delay_days",

    ascending=False

)





print(

    sorted_projects[

        [

            "project_name",

            "total_delay_days",

            "acquisition_percent"

        ]

    ].to_string(index=False)

)





# ============================================================

# 8. HIGH-PRIORITY RECORDS

# ============================================================



print("\n" + "=" * 60)

print("HIGH-PRIORITY RECORDS")

print("=" * 60)





# High priority:

# 1. Total delay is greater than 30 days

# 2. Acquisition progress is below 70%



high_priority = df[

    (df["total_delay_days"] > 30)

    &

    (df["acquisition_percent"] < 70)

]





print("\nTotal high-priority records:", len(high_priority))



if len(high_priority) > 0:

    print(

        high_priority[

            [

                "project_name",

                "total_delay_days",

                "acquisition_percent"

            ]

        ].to_string(index=False)

    )



else:

    print("No high-priority records found.")





# ============================================================

# 9. RISK SCORE CALCULATION

# ============================================================



print("\n" + "=" * 60)

print("RISK SCORE CALCULATION")

print("=" * 60)





def calculate_risk(row):

    """

    Calculate a rule-based risk score.



    Maximum score: 100 points



    Factors:

    - Total delay

    - Payment delay

    - Ownership dispute

    - Approval delay

    - Rehabilitation status

    - Acquisition progress

    """



    score = 0



    total_delay = row["total_delay_days"]

    payment_delay = row["payment_delay_days"]

    approval_delay = row["approval_delay_days"]

    acquisition_percent = row["acquisition_percent"]



    ownership_dispute = str(

        row["ownership_dispute"]

    ).lower()



    dispute_level = str(

        row["dispute_level"]

    ).lower()



    rehabilitation_status = str(

        row["rehabilitation_status"]

    ).lower()





    # --------------------------------------------------------

    # Total delay: Maximum 25 points

    # --------------------------------------------------------



    if total_delay > 60:

        score += 25



    elif total_delay > 30:

        score += 15





    # --------------------------------------------------------

    # Payment delay: Maximum 20 points

    # --------------------------------------------------------



    if payment_delay > 60:

        score += 20



    elif payment_delay > 30:

        score += 15



    elif payment_delay > 15:

        score += 10





    # --------------------------------------------------------

    # Ownership dispute: Maximum 20 points

    # --------------------------------------------------------



    if ownership_dispute == "yes":



        if dispute_level in ["high", "severe"]:

            score += 20



        elif dispute_level in ["medium", "moderate"]:

            score += 15



        else:

            score += 10





    # --------------------------------------------------------

    # Approval delay: Maximum 15 points

    # --------------------------------------------------------



    if approval_delay > 60:

        score += 15



    elif approval_delay > 30:

        score += 10



    elif approval_delay > 15:

        score += 7





    # --------------------------------------------------------

    # Rehabilitation status: Maximum 10 points

    # --------------------------------------------------------



    if rehabilitation_status in [

        "pending",

        "delayed",

        "not started"

    ]:

        score += 10





    # --------------------------------------------------------

    # Acquisition percentage: Maximum 10 points

    # --------------------------------------------------------



    if acquisition_percent < 40:

        score += 10



    elif acquisition_percent < 70:

        score += 7





    return score

def calculate_risk_breakdown(row):
    """
    Return the individual point contributions for every risk factor.
    These rules match calculate_risk().
    """
    breakdown = {
        "total_delay_points": 0,
        "payment_delay_points": 0,
        "ownership_dispute_points": 0,
        "approval_delay_points": 0,
        "rehabilitation_points": 0,
        "acquisition_progress_points": 0,
    }

    total_delay = row["total_delay_days"]
    payment_delay = row["payment_delay_days"]
    approval_delay = row["approval_delay_days"]
    acquisition_percent = row["acquisition_percent"]

    ownership_dispute = str(row["ownership_dispute"]).lower()
    dispute_level = str(row["dispute_level"]).lower()
    rehabilitation_status = str(row["rehabilitation_status"]).lower()

    if total_delay > 60:
        breakdown["total_delay_points"] = 25
    elif total_delay > 30:
        breakdown["total_delay_points"] = 15

    if payment_delay > 60:
        breakdown["payment_delay_points"] = 20
    elif payment_delay > 30:
        breakdown["payment_delay_points"] = 15
    elif payment_delay > 15:
        breakdown["payment_delay_points"] = 10

    if ownership_dispute == "yes":
        if dispute_level in ["high", "severe"]:
            breakdown["ownership_dispute_points"] = 20
        elif dispute_level in ["medium", "moderate"]:
            breakdown["ownership_dispute_points"] = 15
        else:
            breakdown["ownership_dispute_points"] = 10

    if approval_delay > 60:
        breakdown["approval_delay_points"] = 15
    elif approval_delay > 30:
        breakdown["approval_delay_points"] = 10
    elif approval_delay > 15:
        breakdown["approval_delay_points"] = 7

    if rehabilitation_status in ["pending", "delayed", "not started"]:
        breakdown["rehabilitation_points"] = 10

    if acquisition_percent < 40:
        breakdown["acquisition_progress_points"] = 10
    elif acquisition_percent < 70:
        breakdown["acquisition_progress_points"] = 7

    return breakdown


def get_risk_factor_label(factor_key):
    labels = {
        "total_delay_points": "Total delay",
        "payment_delay_points": "Payment delay",
        "ownership_dispute_points": "Ownership dispute",
        "approval_delay_points": "Approval delay",
        "rehabilitation_points": "Rehabilitation",
        "acquisition_progress_points": "Low acquisition progress",
    }
    return labels.get(factor_key, factor_key.replace("_points", "").replace("_", " ").title())


def get_contributing_risk_factors(breakdown):
    """Return all non-zero risk factors except the primary factor."""
    ranked = sorted(breakdown.items(), key=lambda item: item[1], reverse=True)
    non_zero = [item for item in ranked if item[1] > 0]
    if len(non_zero) <= 1:
        return "None"
    return ", ".join(get_risk_factor_label(key) for key, _ in non_zero[1:])


def get_recommended_action(row):
    """Create one concise action based on the dominant risk factor."""
    factor = row["dominant_risk_factor"]
    actions = {
        "Total delay": "Review the project timeline and escalate long-pending activities.",
        "Payment delay": "Prioritize compensation verification and payment processing.",
        "Ownership dispute": "Escalate the ownership dispute for legal and administrative review.",
        "Approval delay": "Coordinate with the approving authority to clear pending approvals.",
        "Rehabilitation": "Prioritize rehabilitation and resettlement activities for affected people.",
        "Low acquisition progress": "Prepare an intervention plan to improve land acquisition progress.",
        "No major risk factor": "Continue regular monitoring and periodic review.",
    }
    return actions.get(factor, "Conduct a detailed administrative review.")


def get_dominant_risk_factor(breakdown):
    """Return the factor with the highest point contribution."""
    factor_names = {
        "total_delay_points": "Total delay",
        "payment_delay_points": "Payment delay",
        "ownership_dispute_points": "Ownership dispute",
        "approval_delay_points": "Approval delay",
        "rehabilitation_points": "Rehabilitation",
        "acquisition_progress_points": "Low acquisition progress",
    }

    dominant_key = max(breakdown, key=breakdown.get)

    if breakdown[dominant_key] == 0:
        return "No major risk factor"

    return factor_names[dominant_key]






# Apply the risk score calculation

df["risk_score"] = df.apply(

    calculate_risk,

    axis=1

)

# Calculate and store individual risk-factor contributions.
df["risk_breakdown"] = df.apply(calculate_risk_breakdown, axis=1)
df["dominant_risk_factor"] = df["risk_breakdown"].apply(get_dominant_risk_factor)
df["contributing_risk_factors"] = df["risk_breakdown"].apply(get_contributing_risk_factors)

for factor in [
    "total_delay_points",
    "payment_delay_points",
    "ownership_dispute_points",
    "approval_delay_points",
    "rehabilitation_points",
    "acquisition_progress_points",
]:
    df[factor] = df["risk_breakdown"].apply(lambda breakdown: breakdown[factor])






print("\n--- RISK SCORES ---")



print(

    df[

        [

            "project_name",

            "risk_score"

        ]

    ].to_string(index=False)

)





# ============================================================

# 10. RISK LEVEL CLASSIFICATION

# ============================================================



print("\n" + "=" * 60)

print("RISK LEVEL CLASSIFICATION")

print("=" * 60)





def get_risk_level(score):

    """

    Convert the numerical risk score into a risk category.

    """



    if score >= 60:

        return "HIGH"



    elif score >= 30:

        return "MEDIUM"



    else:

        return "LOW"





df["risk_level"] = df["risk_score"].apply(

    get_risk_level

)







# ============================================================

# RISK SCORE DISTRIBUTION ANALYSIS

# ============================================================



print("\n" + "=" * 60)

print("RISK SCORE DISTRIBUTION ANALYSIS")

print("=" * 60)





# Display statistical information about risk scores

print("\n--- RISK SCORE STATISTICS ---")



print(

    df["risk_score"].describe()

)





# Count records in each risk category

print("\n--- RISK LEVEL COUNTS ---")



print(

    df["risk_level"].value_counts()

)





# Display the lowest and highest risk scores

print("\n--- SCORE RANGE ---")



print("Minimum risk score:", df["risk_score"].min())

print("Maximum risk score:", df["risk_score"].max())





# Display how many records are close to the HIGH threshold

print("\n--- RECORDS NEAR HIGH-RISK THRESHOLD ---")



near_high_risk = df[

    (df["risk_score"] >= 50)

    &

    (df["risk_score"] < 60)

]



print(

    "Records with scores between 50 and 59:",

    len(near_high_risk)

)





# Display the highest-risk records

print("\n--- TOP 20 HIGHEST-RISK RECORDS ---")



top_risk_records = df.sort_values(

    by="risk_score",

    ascending=False

).head(20)





print(

    top_risk_records[

        [

            "project_name",

            "risk_score",
        "risk_level",
        "dominant_risk_factor",
        "total_delay_points",
        "payment_delay_points",
        "ownership_dispute_points",
        "approval_delay_points",
        "rehabilitation_points",
        "acquisition_progress_points",

            "total_delay_days",

            "payment_delay_days",

            "approval_delay_days",

            "acquisition_percent"

        ]

    ].to_string(index=False)

)







print("\n--- RISK ASSESSMENT ---")



print(

    df[

        [

            "project_name",

            "risk_score",

            "risk_level"

        ]

    ].to_string(index=False)

)





# ============================================================

# 11. EXPLAINABLE RISK REASONS

# ============================================================



print("\n" + "=" * 60)

print("EXPLAINABLE RISK REASONS")

print("=" * 60)





def get_reasons(row):

    """

    Generate human-readable reasons for the risk score.

    """



    reasons = []





    # Total delay reasons

    if row["total_delay_days"] > 60:

        reasons.append(

            "Project has a significant total delay"

        )



    elif row["total_delay_days"] > 30:

        reasons.append(

            "Project has been delayed for more than 30 days"

        )





    # Payment delay reasons

    if row["payment_delay_days"] > 60:

        reasons.append(

            "Very high payment delay"

        )



    elif row["payment_delay_days"] > 30:

        reasons.append(

            "High payment delay"

        )



    elif row["payment_delay_days"] > 15:

        reasons.append(

            "Payment process is delayed"

        )





    # Ownership dispute reasons

    if str(row["ownership_dispute"]).lower() == "yes":



        dispute_level = str(

            row["dispute_level"]

        ).lower()



        if dispute_level in ["high", "severe"]:

            reasons.append(

                "High-level ownership dispute detected"

            )



        elif dispute_level in ["medium", "moderate"]:

            reasons.append(

                "Moderate ownership dispute detected"

            )



        else:

            reasons.append(

                "Ownership dispute is present"

            )





    # Approval delay reasons

    if row["approval_delay_days"] > 60:

        reasons.append(

            "Approval process has a significant delay"

        )



    elif row["approval_delay_days"] > 30:

        reasons.append(

            "Approval process is delayed"

        )



    elif row["approval_delay_days"] > 15:

        reasons.append(

            "Approval process requires monitoring"

        )





    # Rehabilitation reasons

    rehabilitation_status = str(

        row["rehabilitation_status"]

    ).lower()



    if rehabilitation_status in [

        "pending",

        "delayed",

        "not started"

    ]:

        reasons.append(

            "Rehabilitation and resettlement process is pending"

        )





    # Acquisition progress reasons

    if row["acquisition_percent"] < 40:

        reasons.append(

            "Land acquisition progress is very low"

        )



    elif row["acquisition_percent"] < 70:

        reasons.append(

            "Land acquisition progress is below 70%"

        )





    return reasons





df["reasons"] = df.apply(

    get_reasons,

    axis=1

)





for index, row in df.iterrows():



    print("\nRecord:", row["project_name"])

    print("Risk:", row["risk_level"])

    print("Risk Score:", row["risk_score"])



    print("Reasons:")



    if len(row["reasons"]) == 0:

        print("- No major risk factors detected")



    else:

        for reason in row["reasons"]:

            print("-", reason)





# ============================================================

# 12. RISK FACTOR BREAKDOWN
# ============================================================

print("\n" + "=" * 60)
print("RISK FACTOR BREAKDOWN")
print("=" * 60)

for index, row in df.head(10).iterrows():
    print("\nRecord:", row["project_name"])
    print("Risk Score:", row["risk_score"])
    print("Dominant Risk Factor:", row["dominant_risk_factor"])
    print("Contributions:")

    breakdown = row["risk_breakdown"]
    has_contribution = False

    for factor, points in breakdown.items():
        if points > 0:
            label = factor.replace("_points", "").replace("_", " ").title()
            print(f"- {label}: +{points}")
            has_contribution = True

    if not has_contribution:
        print("- No major risk factors")


# 12. RECOMMENDATION ENGINE

# ============================================================



print("\n" + "=" * 60)

print("RECOMMENDATION ENGINE")

print("=" * 60)





def get_recommendations(row):

    """

    Generate recommendations based on detected risk factors.

    """



    recommendations = []





    # Payment recommendation

    if row["payment_delay_days"] > 15:

        recommendations.append(

            "Prioritize payment verification and compensation processing"

        )





    # Ownership dispute recommendation

    if str(row["ownership_dispute"]).lower() == "yes":

        recommendations.append(

            "Escalate ownership disputes for legal review"

        )





    # Approval recommendation

    if row["approval_delay_days"] > 15:

        recommendations.append(

            "Escalate the delayed approval process"

        )





    # Rehabilitation recommendation

    rehabilitation_status = str(

        row["rehabilitation_status"]

    ).lower()



    if rehabilitation_status in [

        "pending",

        "delayed",

        "not started"

    ]:

        recommendations.append(

            "Prioritize rehabilitation and resettlement activities"

        )





    # Acquisition progress recommendation

    if (

        row["total_delay_days"] > 30

        and row["acquisition_percent"] < 70

    ):

        recommendations.append(

            "Consider immediate intervention to improve acquisition progress"

        )





    # General high-risk recommendation

    if row["risk_score"] >= 60:

        recommendations.append(

            "Escalate record for immediate administrative review"

        )





    # Default recommendation

    if len(recommendations) == 0:

        recommendations.append(

            "Continue regular monitoring"

        )





    return recommendations





df["recommendations"] = df.apply(

    get_recommendations,

    axis=1

)

df["recommended_action"] = df.apply(get_recommended_action, axis=1)


def get_priority_level(row):
    """Assign an administrative priority using risk level and contributing factors."""
    non_zero_factors = sum(
        1 for value in row[
            [
                "total_delay_points",
                "payment_delay_points",
                "ownership_dispute_points",
                "approval_delay_points",
                "rehabilitation_points",
                "acquisition_progress_points",
            ]
        ] if value > 0
    )

    if row["risk_level"] == "HIGH" and non_zero_factors >= 3:
        return "URGENT"
    if row["risk_level"] == "HIGH":
        return "HIGH"
    if row["risk_level"] == "MEDIUM":
        return "MEDIUM"
    return "LOW"


df["priority_level"] = df.apply(get_priority_level, axis=1)





for index, row in df.iterrows():



    print("\nRecord:", row["project_name"])

    print("Risk:", row["risk_level"])



    print("Recommended Actions:")



    for recommendation in row["recommendations"]:

        print("-", recommendation)





# ============================================================

# 13. FINAL SUMMARY TABLE

# ============================================================



print("\n" + "=" * 60)

print("FINAL RISK SUMMARY")

print("=" * 60)





summary_df = df[

    [

        "land_id",

        "state",

        "district",

        "project_name",

        "total_delay_days",

        "acquisition_percent",

        "risk_score",

        "risk_level"

    ]

]





print(

    summary_df.to_string(index=False)

)





# ============================================================

# 14. RISK LEVEL SUMMARY

# ============================================================



print("\n" + "=" * 60)

print("RISK LEVEL SUMMARY")

print("=" * 60)





risk_summary = (

    df["risk_level"]

    .value_counts()

    .reindex(

        ["HIGH", "MEDIUM", "LOW"],

        fill_value=0

    )

)





print(risk_summary)





# ============================================================

# 15. EXPORT RESULTS

# ============================================================



print("\n" + "=" * 60)

print("EXPORTING RESULTS")

print("=" * 60)





# Create a copy for exporting

export_df = df.copy()





# Convert risk breakdown dictionary into readable text.
export_df["risk_breakdown"] = export_df["risk_breakdown"].apply(
    lambda breakdown: "; ".join(
        f"{factor.replace('_points', '').replace('_', ' ').title()}: {points}"
        for factor, points in breakdown.items()
        if points > 0
    ) or "No major risk factors"
)

# Convert list values into readable text

export_df["reasons"] = export_df["reasons"].apply(

    lambda reasons: "; ".join(reasons)

)





export_df["recommendations"] = export_df[

    "recommendations"

].apply(

    lambda recommendations: "; ".join(recommendations)

)





# Save output inside the delay predictor folder

output_file = CURRENT_DIR / "risk_analysis_results.csv"





export_df.to_csv(

    output_file,

    index=False

)





print("\nRisk analysis results exported successfully!")

print("Output file:", output_file)





# ============================================================

# 16. PROGRAM COMPLETED

# ============================================================



print("\n" + "=" * 60)

print("ANALYSIS COMPLETED SUCCESSFULLY")

print("=" * 60)
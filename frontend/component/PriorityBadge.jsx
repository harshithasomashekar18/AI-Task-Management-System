const tone = { HIGH: "danger", MEDIUM: "warning", LOW: "secondary" };

export default function PriorityBadge({ task }) {
  const priority = task.priority?.toUpperCase();
  if (!priority) return null;
  const source = task.prioritySource === "AI" ? "AI" : task.prioritySource === "RULES" ? "rules" : null;
  return <span className={`badge text-bg-${tone[priority] || "secondary"}`}>
    {priority} priority{source ? ` · ${source}` : ""}
  </span>;
}

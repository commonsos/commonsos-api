UPDATE transactions SET blockchain_completed_at = NOW() WHERE blockchain_completed_at IS NULL
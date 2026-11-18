// All photography lives here. Swap any id/URL for your own photos (or files in /public).
const u = (id, w = 1400) => `https://images.unsplash.com/${id}?auto=format&fit=crop&w=${w}&q=80`;

export const photo = {
  heroMountain: u('photo-1464822759023-fed622ff2c3b', 2000),
  valley: u('photo-1506905925346-21bda4d32df4', 1600),
  lake: u('photo-1501785888041-af3ef285b470', 1600),
  forest: u('photo-1448375240586-882707db888b', 1400),
  snowRange: u('photo-1454496522488-7a8e488e8606', 1600),
  cafeInterior: u('photo-1554118811-1e0d58224f24', 1400),
  cafeFriends: u('photo-1521017432531-fbd92d768814', 1400),
  cafeTalk: u('photo-1445116572660-236099ec97a0', 1400),
  cafeCosy: u('photo-1517248135467-4c7edcad34c4', 1400),
  coffee: u('photo-1495474472287-4d71bcdd2085', 1000),
};

// category tile photos + last-resort image when a dish photo fails to load
export const categoryPhoto = {
  Pizza: u('photo-1579751626657-72bc17010498', 900),
  Burgers: u('photo-1568901346375-23c9450c58cd', 900),
  Biryani: u('photo-1563379091339-03246963d96c', 900),
  Desserts: u('photo-1488477181946-6428a0291777', 900),
  Drinks: u('photo-1461023058943-07fcbe16d735', 900),
};
export const defaultDishPhoto = u('photo-1495474472287-4d71bcdd2085', 900);
export const categoryNote = {
  Pizza: 'Thin crust, slow-fermented, wood-fire in spirit.',
  Burgers: 'Stacked tall, made for mountain-sized hunger.',
  Biryani: 'Sealed handi, slow dum, saffron steam.',
  Desserts: 'Warm, sweet, and gone too soon.',
  Drinks: 'Chai, coffee and cold things for every altitude.',
};
